/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.kuschku.libquassel.client.ClientData;
import de.kuschku.libquassel.client.QClient;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.events.HandshakeFailedEvent;
import de.kuschku.libquassel.events.UnknownCertificateEvent;
import de.kuschku.libquassel.functions.types.HandshakeFunction;
import de.kuschku.libquassel.functions.types.Heartbeat;
import de.kuschku.libquassel.objects.types.ClientInit;
import de.kuschku.libquassel.primitives.QMetaTypeRegistry;
import de.kuschku.libquassel.primitives.serializers.ProtocolSerializer;
import de.kuschku.libquassel.primitives.types.Protocol;
import de.kuschku.libquassel.protocols.DatastreamPeer;
import de.kuschku.libquassel.protocols.LegacyPeer;
import de.kuschku.libquassel.protocols.RemotePeer;
import de.kuschku.libquassel.ssl.CertificateManager;
import de.kuschku.libquassel.ssl.UnknownCertificateException;
import de.kuschku.util.ServerAddress;
import de.kuschku.util.niohelpers.WrappedChannel;

import static de.kuschku.libquassel.primitives.QMetaType.Type.UInt;
import static de.kuschku.util.AndroidAssert.assertNotNull;

/**
 * Starts a connection to a core and handles the data in the backend.
 * Provides a Client object for interacting with a friendly tree structure of the data.
 *
 * @author Janne Koschinski
 */
public class CoreConnection {

    @NonNull
    private final ServerAddress address;
    @NonNull
    private final ClientData clientData;
    @NonNull
    private final BusProvider busProvider;
    @NonNull
    private final QClient client;
    @NonNull
    private final CertificateManager certificateManager;
    @Nullable
    private ExecutorService outputExecutor;
    @Nullable
    private EndableThread inputThread;
    @Nullable
    private EndableThread heartbeatThread;
    @Nullable
    private RemotePeer remotePeer;
    @Nullable
    private WrappedChannel channel;
    @Nullable
    private Socket socket;
    @NonNull
    private ConnectionChangeEvent.Status status = ConnectionChangeEvent.Status.DISCONNECTED;

    public CoreConnection(@NonNull final ServerAddress address,
                          @NonNull final ClientData clientData,
                          @NonNull final BusProvider busProvider,
                          @NonNull final QClient client,
                          @NonNull CertificateManager certificateManager) {
        this.address = address;
        this.clientData = clientData;
        this.busProvider = busProvider;
        this.client = client;
        this.certificateManager = certificateManager;
    }

    @NonNull
    public ConnectionChangeEvent.Status getStatus() {
        return status;
    }

    /**
     * This method opens a socket to the specified address and starts the connection process.
     *
     * @param supportsKeepAlive If the connection may use keepAlive
     * @throws IOException
     */
    public void open(boolean supportsKeepAlive) throws IOException {
        assertNotNull(client);

        status = ConnectionChangeEvent.Status.HANDSHAKE;
        client.setConnectionStatus(status);

        // Intialize socket
        socket = new Socket();
        if (supportsKeepAlive) socket.setKeepAlive(true);
        socket.connect(new InetSocketAddress(address.host, address.port), 10000);

        // Wrap socket in channel for nio functions
        channel = WrappedChannel.ofSocket(socket);

        busProvider.event.register(this);

        // Create executor for write events
        outputExecutor = Executors.newSingleThreadExecutor();

        // Execute handshake
        handshake();
    }

    /**
     * Closes the connection and interrupts all threads this connection has spawned.
     */
    public void close() {
        assertNotNull(client);

        client.setConnectionStatus(ConnectionChangeEvent.Status.DISCONNECTED);

        // We can do this because we clean up the file handles ourselves
        if (inputThread != null) inputThread.end();
        if (heartbeatThread != null) heartbeatThread.end();
        if (outputExecutor != null) outputExecutor.shutdownNow();

        // Which we do exactly here
        try {
            if (channel != null) channel.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            // We won’t report these issues, as these don’t matter to us anyway anymore
        }
    }

    @Nullable
    public ExecutorService getOutputExecutor() {
        return outputExecutor;
    }

    @NonNull
    public ClientData getClientData() {
        return clientData;
    }

    @NonNull
    public WrappedChannel getChannel() {
        assertNotNull(channel);

        return channel;
    }

    @Nullable
    public RemotePeer getRemotePeer() {
        return remotePeer;
    }

    @Nullable
    public Socket getSocket() {
        return socket;
    }

    /**
     * Starts the first handshake phase with negotiating the protocol and if SSL or compression are to be used.
     *
     * @throws IOException
     */
    private void handshake() throws IOException {
        assertNotNull(channel);

        // Start protocol handshake with magic version and feature flags
        QMetaTypeRegistry.serialize(UInt, channel, 0x42b33f00 | clientData.flags.flags);

        // Send list of supported protocols
        for (int supportedProtocol : clientData.supportedProtocols) {
            QMetaTypeRegistry.serialize(UInt, channel, supportedProtocol);
        }
        QMetaTypeRegistry.serialize(UInt, channel, 0x01 << 31);

        // Spawn and start a new read thread
        inputThread = new ReadThread();
        heartbeatThread = new HeartbeatThread();
        inputThread.start();
    }

    public void onEventAsync(HandshakeFailedEvent event) {
        this.close();
    }

    public void onEvent(@NonNull ConnectionChangeEvent event) {
        this.status = event.status;
        if (event.status == ConnectionChangeEvent.Status.INITIALIZING_DATA && heartbeatThread != null)
            heartbeatThread.start();
    }

    public void setCompression(boolean supportsCompression) {
        if (supportsCompression)
            channel = WrappedChannel.withCompression(getChannel());
    }

    private void setSSL(boolean supportsSSL) {
        if (supportsSSL) {
            try {
                channel = WrappedChannel.withSSL(getChannel(), certificateManager, address);
            } catch (Exception e) {
                if (e.getCause() instanceof UnknownCertificateException) {
                    busProvider.sendEvent(new UnknownCertificateEvent((UnknownCertificateException) e.getCause()));
                } else {
                    busProvider.sendEvent(new GeneralErrorEvent(e));
                }
                close();
            }
        }
    }

    /**
     * A runnable that reads from the channel and calls the functions responsible for processing the read data.
     */
    private class ReadThread extends EndableThread {
        private boolean running = true;

        public ReadThread() {
            setName(getClass().getSimpleName());
        }

        @Override
        public void run() {
            assertNotNull(client);

            try {
                boolean hasReadPreHandshake = false;
                while (running) {
                    if (!hasReadPreHandshake) {
                        final ByteBuffer buffer = ByteBuffer.allocate(4);
                        assertNotNull(buffer);
                        getChannel().read(buffer);

                        final Protocol protocol = ProtocolSerializer.get().deserialize(buffer);

                        // Wrap socket in SSL context if ssl is enabled
                        setSSL(protocol.protocolFlags.supportsSSL);
                        // Wrap socket in deflater if compression is enabled
                        setCompression(protocol.protocolFlags.supportsCompression);

                        // Initialize remote peer
                        switch (protocol.protocolVersion) {
                            case 0x01:
                                remotePeer = new LegacyPeer(CoreConnection.this, busProvider);
                                break;
                            case 0x02:
                                remotePeer = new DatastreamPeer(CoreConnection.this, busProvider);
                                break;
                            default:
                                busProvider.sendEvent(new HandshakeFailedEvent("Core too new: Protocol Unsupported"));
                                close();
                                return;
                        }

                        // Mark prehandshake as read
                        hasReadPreHandshake = true;
                        assertNotNull(heartbeatThread);

                        // Send client data to core
                        String clientDate = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.US).format(new Date());
                        assertNotNull(clientDate);
                        busProvider.dispatch(new HandshakeFunction(new ClientInit(
                                clientDate,
                                protocol.protocolFlags.supportsSSL,
                                getClientData().identifier,
                                false,
                                getClientData().protocolVersion
                        )));
                    } else {
                        remotePeer.processMessage();
                    }
                }
            } catch (SocketException e) {
                Log.e("libquassel", "Socket closed while reading");
                client.setConnectionStatus(ConnectionChangeEvent.Status.DISCONNECTED);
            } catch (Exception e) {
                busProvider.sendEvent(new GeneralErrorEvent(e));
            }
        }

        @Override
        public void end() {
            running = false;
        }
    }

    private class HeartbeatThread extends EndableThread {
        private boolean running = true;

        public HeartbeatThread() {
            setName(getClass().getSimpleName());
        }

        @Override
        public void run() {
            try {
                assertNotNull(client);

                while (running) {
                    Heartbeat heartbeat = new Heartbeat();
                    busProvider.dispatch(heartbeat);

                    Thread.sleep(30 * 1000);
                }
            } catch (InterruptedException e) {
            }
        }

        @Override
        public void end() {
            running = false;
        }
    }

    private abstract class EndableThread extends Thread {
        public abstract void end();
    }
}
