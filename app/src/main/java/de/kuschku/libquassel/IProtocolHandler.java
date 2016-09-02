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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.kuschku.libquassel.client.Client;
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
import de.kuschku.libquassel.objects.types.SessionInit;

public interface IProtocolHandler {
    @Subscribe(threadMode = ThreadMode.MAIN)
    void onEventMainThread(InitDataFunction packedFunc);

    @Subscribe(threadMode = ThreadMode.MAIN)
    void onEventMainThread(InitRequestFunction packedFunc);

    @Subscribe(threadMode = ThreadMode.MAIN)
    void onEventMainThread(RpcCallFunction packedFunc);

    @Subscribe(threadMode = ThreadMode.MAIN)
    void onEventMainThread(SyncFunction packedFunc);

    @Subscribe
    void onEvent(ClientInitReject message);

    @Subscribe
    void onEvent(ClientInitAck message);

    @Subscribe
    void onEvent(ClientLoginAck message);

    @Subscribe
    void onEvent(ClientLoginReject message);

    @Subscribe
    void onEvent(SessionInit message);

    @Subscribe
    void onEvent(Heartbeat message);

    @Subscribe(threadMode = ThreadMode.MAIN)
    void onEventMainThread(HeartbeatReply message);

    @NonNull
    Client getClient();
}
