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
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.CoreSetupRequiredEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.events.HandshakeFailedEvent;
import de.kuschku.libquassel.events.LoginRequireEvent;
import de.kuschku.libquassel.functions.types.Heartbeat;
import de.kuschku.libquassel.functions.types.HeartbeatReply;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.objects.types.ClientInitAck;
import de.kuschku.libquassel.objects.types.ClientInitReject;
import de.kuschku.libquassel.objects.types.ClientLoginAck;
import de.kuschku.libquassel.objects.types.ClientLoginReject;
import de.kuschku.libquassel.objects.types.CoreSetupAck;
import de.kuschku.libquassel.objects.types.SessionInit;
import de.kuschku.libquassel.syncables.SyncableRegistry;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.invokers.IClient;
import de.kuschku.libquassel.syncables.types.invokers.InvokerRegistry;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class ProtocolHandler implements IProtocolHandler {
    @NonNull
    public final Client client;
    @NonNull
    private final BusProvider busProvider;

    public ProtocolHandler(@NonNull BusProvider busProvider, @NonNull Client client) {
        this.busProvider = busProvider;
        this.busProvider.handle.register(this);
        this.busProvider.event.register(this);
        this.client = client;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull InitDataFunction packedFunc) {
        try {
            SyncableObject object = SyncableRegistry.from(packedFunc);
            assertNotNull(object);

            client.initObject(packedFunc.className, packedFunc.objectName, object);
        } catch (Exception e) {
            busProvider.sendEvent(new GeneralErrorEvent(e));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull InitRequestFunction packedFunc) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull RpcCallFunction packedFunc) {
        try {
            IClient.get().invoke(packedFunc, client);
        } catch (Exception e) {
            busProvider.sendEvent(new GeneralErrorEvent(e));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull SyncFunction packedFunc) {
        try {
            final Object syncable = client.unsafe_getObjectByIdentifier(packedFunc.className, packedFunc.objectName);

            if (syncable == null) {
                Log.w("ProtocolHandler", String.format("Sync Failed: %s::%s(%s, %s)", packedFunc.className, packedFunc.methodName, packedFunc.objectName, packedFunc.params));
                if (client.connectionStatus() == ConnectionChangeEvent.Status.INITIALIZING_DATA)
                    client.bufferSync(packedFunc);
            } else {
                if (syncable instanceof SyncableObject && !((SyncableObject) syncable).initialized()) {
                    client.initObject(packedFunc.className, packedFunc.objectName, (SyncableObject) syncable);
                } else {
                    InvokerRegistry.invoke(packedFunc, syncable);
                }
            }
        } catch (Exception e) {
            busProvider.sendEvent(new GeneralErrorEvent(e, packedFunc.toString()));
        }
    }

    @Subscribe
    public void onEvent(@NonNull ClientInitReject message) {
        busProvider.sendEvent(new HandshakeFailedEvent(message.Error));
    }

    @Subscribe
    public void onEvent(ClientInitAck message) {
        client.setCore(message);

        if (client.core().Configured) {
            // Send an event to notify that login is necessary
            busProvider.event.postSticky(new LoginRequireEvent(false));
        } else {
            // Send an event to notify that the core is not yet set up
            busProvider.event.postSticky(new CoreSetupRequiredEvent());
        }
    }

    @Subscribe
    public void onEvent(CoreSetupAck message) {
        busProvider.event.postSticky(new LoginRequireEvent(false));
    }

    @Subscribe
    public void onEvent(ClientLoginAck message) {
    }

    @Subscribe
    public void onEvent(@NonNull ClientLoginReject message) {
        busProvider.sendEvent(new LoginRequireEvent(true));
    }

    @Subscribe
    public void onEvent(@NonNull SessionInit message) {
        client.setConnectionStatus(ConnectionChangeEvent.Status.INITIALIZING_DATA);

        client.init(message.SessionState);
    }

    @Subscribe
    public void onEvent(@NonNull Heartbeat heartbeat) {
        busProvider.dispatch(new HeartbeatReply(heartbeat));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull HeartbeatReply heartbeat) {
        DateTime dateTime = DateTime.now().toDateTimeISO();
        Interval interval = new Interval(heartbeat.dateTime, dateTime);
        long roundtrip = interval.toDurationMillis();
        long lag = (long) (roundtrip * 0.5);

        client.setLatency(lag);
    }

    @Subscribe
    public void onEvent(@NonNull ConnectionChangeEvent event) {
    }

    @NonNull
    @Override
    public Client getClient() {
        return client;
    }
}
