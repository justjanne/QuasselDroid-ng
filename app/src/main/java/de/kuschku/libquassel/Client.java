package de.kuschku.libquassel;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.backlogmanagers.BacklogManager;
import de.kuschku.libquassel.backlogmanagers.SimpleBacklogManager;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.localtypes.Buffers;
import de.kuschku.libquassel.objects.types.ClientInitAck;
import de.kuschku.libquassel.objects.types.SessionState;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.primitives.types.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.BufferSyncer;
import de.kuschku.libquassel.syncables.types.BufferViewManager;
import de.kuschku.libquassel.syncables.types.IrcUser;
import de.kuschku.libquassel.syncables.types.Network;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.util.Stream;


public class Client {
    private final Map<Integer, Network> networks = new HashMap<>();
    private final Map<Integer, Buffer> buffers = new HashMap<>();
    private final List<String> initDataQueue = new ArrayList<>();
    private final BacklogManager backlogManager;
    private final BusProvider busProvider;
    private ConnectionChangeEvent.Status connectionStatus;
    private ClientInitAck core;
    private SessionState state;
    private BufferViewManager bufferViewManager;
    private BufferSyncer bufferSyncer;
    private ClientData clientData;

    public int lag;

    public Client(final BusProvider busProvider) {
        this(new SimpleBacklogManager(busProvider), busProvider);
    }

    public Client(final BacklogManager backlogManager, final BusProvider busProvider) {
        this.backlogManager = backlogManager;
        this.busProvider = busProvider;
    }

    public void sendInput(final BufferInfo info, final String input) {
        busProvider.dispatch(new RpcCallFunction(
                "2sendInput(BufferInfo,QString)",
                new QVariant<>(info),
                new QVariant<>(input)
        ));
    }

    public void displayMsg(final Message message) {
        backlogManager.displayMessage(message.bufferInfo.id, message);
    }

    public void putNetwork(final Network network) {
        networks.put(network.getNetworkId(), network);

        for (BufferInfo info : getState().BufferInfos) {
            if (info.networkId == network.getNetworkId()) {
                putBuffer(Buffers.fromType(info, network));
            }
        }
    }

    public Network getNetwork(final int networkId) {
        return this.networks.get(networkId);
    }

    public void putBuffer(final Buffer buffer) {
        this.buffers.put(buffer.getInfo().id, buffer);
    }

    public Buffer getBuffer(final int bufferId) {
        return this.buffers.get(bufferId);
    }

    void sendInitRequest(final String className, final String objectName) {
        sendInitRequest(className, objectName, false);
    }

    void sendInitRequest(final String className, final String objectName, boolean addToList) {
        busProvider.dispatch(new InitRequestFunction(className, objectName));

        if (addToList)
            getInitDataQueue().add(className + ":" + objectName);
    }

    public void __objectRenamed__(String className, String newName, String oldName) {
        getObjectByIdentifier(className, oldName).renameObject(newName);
    }

    public SyncableObject getObjectByIdentifier(final String className, final String objectName) {
        switch (className) {
            case "BacklogManager":
                return getBacklogManager();
            case "IrcChannel": {
                final int networkId = Integer.parseInt(objectName.split("/")[0]);
                final String channelname = objectName.split("/")[1];
                return getNetwork(networkId).getChannels().get(channelname);
            }
            case "BufferSyncer":
                return bufferSyncer;
            case "BufferViewConfig":
                return getBufferViewManager().BufferViews.get(Integer.valueOf(objectName));
            case "IrcUser": {
                final int networkId = Integer.parseInt(objectName.split("/")[0]);
                final String username = objectName.split("/")[1];
                Network network = getNetwork(networkId);
                return network.getUser(username);
            }
            case "Network": {
                return getNetwork(Integer.parseInt(objectName));
            }
            default:
                throw new IllegalArgumentException(String.format("No object of type %s known: %s", className, objectName));
        }
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public List<String> getInitDataQueue() {
        return initDataQueue;
    }

    public BacklogManager getBacklogManager() {
        return backlogManager;
    }

    public BufferViewManager getBufferViewManager() {
        return bufferViewManager;
    }

    public void setBufferViewManager(final BufferViewManager bufferViewManager) {
        this.bufferViewManager = bufferViewManager;
        for (int id : bufferViewManager.BufferViews.keySet()) {
            sendInitRequest("BufferViewConfig", String.valueOf(id), true);
        }
    }

    public BufferSyncer getBufferSyncer() {
        return bufferSyncer;
    }

    public void setBufferSyncer(BufferSyncer bufferSyncer) {
        this.bufferSyncer = bufferSyncer;
    }

    public ClientInitAck getCore() {
        return core;
    }

    public void setCore(ClientInitAck core) {
        this.core = core;
    }

    public void setConnectionStatus(final ConnectionChangeEvent.Status connectionStatus) {
        this.connectionStatus = connectionStatus;
        busProvider.sendEvent(new ConnectionChangeEvent(connectionStatus));
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public Collection<Buffer> getBuffers(int networkId) {
        return new Stream<>(this.buffers.values()).filter(buffer -> buffer.getInfo().networkId == networkId).list();
    }

    public Collection<Network> getNetworks() {
        return networks.values();
    }

    public ConnectionChangeEvent.Status getConnectionStatus() {
        return connectionStatus;
    }
}
