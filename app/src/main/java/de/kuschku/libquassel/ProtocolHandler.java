package de.kuschku.libquassel;

import android.util.Log;

import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.events.HandshakeFailedEvent;
import de.kuschku.libquassel.events.LoginFailedEvent;
import de.kuschku.libquassel.events.LoginSuccessfulEvent;
import de.kuschku.libquassel.exceptions.UnknownTypeException;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.objects.types.ClientInitAck;
import de.kuschku.libquassel.objects.types.ClientInitReject;
import de.kuschku.libquassel.objects.types.ClientLoginAck;
import de.kuschku.libquassel.objects.types.ClientLoginReject;
import de.kuschku.libquassel.objects.types.SessionInit;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.SyncableRegistry;
import de.kuschku.util.ReflectionUtils;

public class ProtocolHandler implements IProtocolHandler {
    public final Client client;
    private BusProvider busProvider;

    public ProtocolHandler(BusProvider busProvider) {
        this.busProvider = busProvider;
        this.busProvider.handle.register(this);
        this.busProvider.event.register(this);
        this.client = new Client(busProvider);
    }

    public void onEventMainThread(InitDataFunction packedFunc) {
        try {
            if (client.getConnectionStatus() == ConnectionChangeEvent.Status.CONNECTED) {
                if (!packedFunc.className.equals("IrcUser"))
                    Log.e("libquassel", "Late Receive! " + packedFunc.toString());
            } else {
                if (client.getInitDataQueue().contains(packedFunc.className + ":" + packedFunc.objectName)) {
                    client.getInitDataQueue().remove(packedFunc.className + ":" + packedFunc.objectName);
                    if (client.getInitDataQueue().isEmpty()) {
                        client.setConnectionStatus(ConnectionChangeEvent.Status.CONNECTED);
                    }
                }
            }
            SyncableRegistry.from(packedFunc).init(packedFunc, busProvider, client);
        } catch (Exception e) {
            busProvider.sendEvent(new GeneralErrorEvent(e));
        }
    }

    public void onEventMainThread(InitRequestFunction packedFunc) {
    }

    public void onEventMainThread(RpcCallFunction packedFunc) {
        try {
            if (packedFunc.functionName.substring(0, 1).equals("2")) {
                ReflectionUtils.invokeMethod(client, packedFunc.functionName.substring(1), packedFunc.params);
            } else if (packedFunc.functionName.equals("__objectRenamed__")) {
                ReflectionUtils.invokeMethod(client, packedFunc.functionName, packedFunc.params);
            } else {
                throw new IllegalArgumentException("Unknown type: " + packedFunc.functionName);
            }
        } catch (Exception e) {
            busProvider.sendEvent(new GeneralErrorEvent(e));
        }
    }

    public void onEventMainThread(SyncFunction packedFunc) {
        try {
            final Object syncable = client.getObjectByIdentifier(packedFunc.className, packedFunc.objectName);
            if (syncable != null) {
                ReflectionUtils.invokeMethod(syncable, packedFunc.methodName, packedFunc.params);
            } else {
                busProvider.sendEvent(new GeneralErrorEvent(new UnknownTypeException(packedFunc.className)));
            }
        } catch (Exception e) {
            busProvider.sendEvent(new GeneralErrorEvent(e));
        }
    }

    public void onEventMainThread(ClientInitReject message) {
        busProvider.sendEvent(new HandshakeFailedEvent(message.Error));
    }

    public void onEventMainThread(ClientInitAck message) {
        client.setCore(message);

        if (client.getCore().Configured) {
            // Send an event to notify that login is necessary
            client.setConnectionStatus(ConnectionChangeEvent.Status.LOGIN_REQUIRED);
        } else {
            // Send an event to notify that the core is not yet set up
            client.setConnectionStatus(ConnectionChangeEvent.Status.CORE_SETUP_REQUIRED);
        }
    }

    public void onEventMainThread(ClientLoginAck message) {
        busProvider.sendEvent(new LoginSuccessfulEvent());
        client.setConnectionStatus(ConnectionChangeEvent.Status.CONNECTING);
    }

    public void onEventMainThread(ClientLoginReject message) {
        busProvider.sendEvent(new LoginFailedEvent(message.Error));
    }

    public void onEventMainThread(SessionInit message) {
        client.setState(message.SessionState);

        client.setConnectionStatus(ConnectionChangeEvent.Status.INITIALIZING_DATA);

        client.sendInitRequest("BufferSyncer", "", true);
        client.sendInitRequest("BufferViewManager", "", true);
        client.sendInitRequest("AliasManager", "", true);
        client.sendInitRequest("NetworkConfig", "GlobalNetworkConfig", true);
        client.sendInitRequest("IgnoreListManager", "", true);
        //sendInitRequest("TransferManager", ""); // This thing never gets sent...
        for (int NetworkId : client.getState().NetworkIds) {
            client.sendInitRequest("Network", String.valueOf(NetworkId), true);
        }
        for (BufferInfo info : message.SessionState.BufferInfos) {
            final int initialBacklogCount = 10;
            client.getBacklogManager().requestBacklog(info.id, -1, -1, initialBacklogCount, 0);
        }
    }

    @Override
    public Client getClient() {
        return client;
    }
}
