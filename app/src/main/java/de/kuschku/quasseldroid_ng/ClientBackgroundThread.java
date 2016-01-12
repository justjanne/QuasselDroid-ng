package de.kuschku.quasseldroid_ng;

import java.io.IOException;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.ClientData;
import de.kuschku.libquassel.CoreConnection;
import de.kuschku.libquassel.ProtocolHandler;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.protocols.RemotePeer;
import de.kuschku.quasseldroid_ng.util.CompatibilityUtils;
import de.kuschku.quasseldroid_ng.util.ServerAddress;

public class ClientBackgroundThread implements Runnable {
    public static final ClientData CLIENT_DATA = new ClientData(
            new ClientData.FeatureFlags(false, true),
            new int[]{RemotePeer.DATASTREAM, RemotePeer.LEGACY},
            "QuasselDroid-ng 0.1 | libquassel 0.2",
            RemotePeer.PROTOCOL_VERSION_LEGACY
    );

    public final BusProvider provider;
    public final CoreConnection connection;
    public final ProtocolHandler handler;

    public ClientBackgroundThread(BusProvider provider, ServerAddress address) {
        this.provider = provider;
        this.connection = new CoreConnection(address, CLIENT_DATA, provider);
        this.handler = new ProtocolHandler(provider);
        this.handler.client.setClientData(CLIENT_DATA);
        this.connection.setClient(handler.client);
    }

    @Override
    public void run() {
        try {
            connection.open(!CompatibilityUtils.isChromiumDevice());
        } catch (IOException e) {
            provider.sendEvent(new GeneralErrorEvent(e));
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (IOException e) {
            provider.sendEvent(new GeneralErrorEvent(e));
        }
    }
}
