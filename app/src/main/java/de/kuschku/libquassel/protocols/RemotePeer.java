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

package de.kuschku.libquassel.protocols;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;

import de.kuschku.libquassel.functions.types.HandshakeFunction;
import de.kuschku.libquassel.functions.types.Heartbeat;
import de.kuschku.libquassel.functions.types.HeartbeatReply;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;

public interface RemotePeer {
    byte DATASTREAM = 0x02;
    byte LEGACY = 0x01;
    int PROTOCOL_VERSION_LEGACY = 10;

    void onEventBackgroundThread(@NonNull SyncFunction func);

    void onEventBackgroundThread(@NonNull RpcCallFunction func);

    void onEventBackgroundThread(@NonNull InitRequestFunction func);

    void onEventBackgroundThread(@NonNull InitDataFunction func);

    void onEventBackgroundThread(@NonNull HandshakeFunction func);

    void onEventBackgroundThread(@NonNull Heartbeat func);

    void onEventBackgroundThread(@NonNull HeartbeatReply func);

    void processMessage() throws IOException;

    @NonNull
    ByteBuffer getBuffer();
}
