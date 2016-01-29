package de.kuschku.libquassel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.events.LagChangedEvent;
import de.kuschku.libquassel.localtypes.NotificationManager;
import de.kuschku.libquassel.localtypes.backlogmanagers.BacklogManager;
import de.kuschku.libquassel.localtypes.backlogmanagers.SimpleBacklogManager;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.StatusMessageEvent;
import de.kuschku.libquassel.functions.types.HandshakeFunction;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.localtypes.Buffers;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.objects.types.ClientInitAck;
import de.kuschku.libquassel.objects.types.ClientLogin;
import de.kuschku.libquassel.objects.types.SessionState;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.BufferSyncer;
import de.kuschku.libquassel.syncables.types.BufferViewConfig;
import de.kuschku.libquassel.syncables.types.BufferViewManager;
import de.kuschku.libquassel.syncables.types.Identity;
import de.kuschku.libquassel.syncables.types.IgnoreListManager;
import de.kuschku.libquassel.syncables.types.IrcChannel;
import de.kuschku.libquassel.syncables.types.IrcUser;
import de.kuschku.libquassel.syncables.types.Network;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.util.backports.Stream;
import de.kuschku.util.observables.lists.ObservableElementList;

import static de.kuschku.util.AndroidAssert.assertNotNull;


public class Client {
    @NonNull
    private final Map<Integer, Network> networks = new HashMap<>();
    @NonNull
    private final ObservableElementList<Integer> networkList = new ObservableElementList<>();
    @NonNull
    private final Map<Integer, Buffer> buffers = new HashMap<>();
    @NonNull
    private final List<String> initDataQueue = new ArrayList<>();
    @NonNull
    private final BacklogManager backlogManager;
    @NonNull
    private final NotificationManager notificationManager = new NotificationManager();
    @NonNull
    private final BusProvider busProvider;
    private long lag;
    private ConnectionChangeEvent.Status connectionStatus;
    private ClientInitAck core;
    @Nullable
    private SessionState state;
    private BufferViewManager bufferViewManager;
    private BufferSyncer bufferSyncer;
    private ClientData clientData;
    private IgnoreListManager ignoreListManager;
    private Map<Integer, Identity> Identities = new HashMap<>();

    public Client(@NonNull final BusProvider busProvider) {
        this(new SimpleBacklogManager(busProvider), busProvider);
    }

    public Client(@NonNull final BacklogManager backlogManager, @NonNull final BusProvider busProvider) {
        this.backlogManager = backlogManager;
        this.busProvider = busProvider;
        this.backlogManager.setClient(this);
    }

    public void sendInput(@NonNull final BufferInfo info, @NonNull final String input) {
        busProvider.dispatch(new RpcCallFunction(
                "2sendInput(BufferInfo,QString)",
                new QVariant<>(info),
                new QVariant<>(input)
        ));
    }

    public void displayMsg(@NonNull final Message message) {
        backlogManager.displayMessage(message.bufferInfo.id, message);
    }

    public void displayStatusMsg(@NonNull String scope, @NonNull String message) {
        busProvider.sendEvent(new StatusMessageEvent(scope, message));
    }

    public void putNetwork(@NonNull final Network network) {
        assertNotNull(state);

        networks.put(network.getNetworkId(), network);
        networkList.add(network.getNetworkId());

        for (BufferInfo info : state.BufferInfos) {
            if (info.networkId == network.getNetworkId()) {
                Buffer buffer = Buffers.fromType(info, network);
                assertNotNull(buffer);

                putBuffer(buffer);
            }
        }
    }

    @Nullable
    public Network getNetwork(final int networkId) {
        return this.networks.get(networkId);
    }

    public void putBuffer(@NonNull final Buffer buffer) {
        this.buffers.put(buffer.getInfo().id, buffer);
        this.notificationManager.init(buffer.getInfo().id);
    }

    @Nullable
    public Buffer getBuffer(final int bufferId) {
        return this.buffers.get(bufferId);
    }

    public void sendInitRequest(@NonNull final String className, @Nullable final String objectName) {
        sendInitRequest(className, objectName, false);
    }

    public void sendInitRequest(@NonNull final String className, @Nullable final String objectName, boolean addToList) {
        busProvider.dispatch(new InitRequestFunction(className, objectName));

        if (addToList)
            getInitDataQueue().add(className + ":" + objectName);
    }

    public void __objectRenamed__(@NonNull String className, @NonNull String newName, @NonNull String oldName) {
        safeGetObjectByIdentifier(className, oldName).renameObject(newName);
    }

    @NonNull
    private SyncableObject safeGetObjectByIdentifier(@NonNull String className, @NonNull String oldName) {
        SyncableObject val = getObjectByIdentifier(className, oldName);
        if (val == null)
            throw new IllegalArgumentException(String.format("Object %s::%s does not exist", className, oldName));
        else return val;
    }

    @Nullable
    public SyncableObject getObjectByIdentifier(@NonNull final String className, @Nullable final String objectName) {
        switch (className) {
            case "BacklogManager":
                return getBacklogManager();
            case "IrcChannel": {
                assertNotNull(objectName);
                final int networkId = Integer.parseInt(objectName.split("/")[0]);
                final String channelname = objectName.split("/")[1];

                // Assert that networkId is valid
                Network network = getNetwork(networkId);
                assertNotNull(network);
                IrcChannel channel = network.getChannels().get(channelname);
                assertNotNull("Channel " + channelname + " not found in " + network.getChannels().keySet(), channel);
                return channel;
            }
            case "BufferSyncer":
                return bufferSyncer;
            case "BufferViewConfig":
                assertNotNull(getBufferViewManager());
                assertNotNull(objectName);
                return getBufferViewManager().BufferViews.get(Integer.valueOf(objectName));
            case "IrcUser": {
                assertNotNull(objectName);
                final int networkId = Integer.parseInt(objectName.split("/")[0]);
                final String username = objectName.split("/")[1];
                Network network = getNetwork(networkId);
                assertNotNull(network);
                IrcUser networkUser = network.getUser(username);
                assertNotNull("User " + username + " not found in " + network.getUsers().keySet(), networkUser);
                return networkUser;
            }
            case "Network": {
                assertNotNull(objectName);
                return getNetwork(Integer.parseInt(objectName));
            }
            default:
                throw new IllegalArgumentException(String.format("No object of type %s known: %s", className, objectName));
        }
    }

    @Nullable
    public SessionState getState() {
        return state;
    }

    public void setState(@Nullable SessionState state) {
        this.state = state;
    }

    @NonNull
    public List<String> getInitDataQueue() {
        return initDataQueue;
    }

    @NonNull
    public BacklogManager<?> getBacklogManager() {
        return backlogManager;
    }

    @Nullable
    public BufferViewManager getBufferViewManager() {
        return bufferViewManager;
    }

    public void setBufferViewManager(@NonNull final BufferViewManager bufferViewManager) {
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

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    @NonNull
    public Collection<Buffer> getBuffers(int networkId) {
        return new Stream<>(this.buffers.values()).filter(buffer -> buffer.getInfo().networkId == networkId).list();
    }

    @NonNull
    public ObservableElementList<Integer> getNetworks() {
        return networkList;
    }

    @NonNull
    public ConnectionChangeEvent.Status getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(@NonNull final ConnectionChangeEvent.Status connectionStatus) {
        this.connectionStatus = connectionStatus;
        busProvider.sendEvent(new ConnectionChangeEvent(connectionStatus));
    }

    public void login(String username, String password) {
        busProvider.dispatch(new HandshakeFunction(new ClientLogin(
                username, password
        )));
    }

    @NonNull
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setLag(long l) {
        lag = l;
        busProvider.sendEvent(new LagChangedEvent(lag));
    }

    public long getLag() {
        return lag;
    }

    public IgnoreListManager getIgnoreListManager() {
        return ignoreListManager;
    }

    public void setIgnoreListManager(IgnoreListManager ignoreListManager) {
        this.ignoreListManager = ignoreListManager;
    }

    public void addIdentity(int id, Identity identity) {
        Identities.put(id, identity);
    }

    public Identity getIdentity(int id) {
        return Identities.get(id);
    }
}
