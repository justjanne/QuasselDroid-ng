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

package de.kuschku.libquassel.client;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.localtypes.buffers.ChannelBuffer;
import de.kuschku.libquassel.localtypes.buffers.QueryBuffer;
import de.kuschku.libquassel.localtypes.buffers.StatusBuffer;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class BufferManager {
    @NonNull
    private final Map<Integer, Buffer> buffers = new HashMap<>();
    private final Client client;

    // We cache those, because the networks might not be initialized at begin
    @NonNull
    private Map<String, Set<BufferInfo>> bufferInfos = new HashMap<>();

    public BufferManager(Client client) {
        this.client = client;
    }

    public void createBuffer(@NonNull Buffer buffer) {
        buffers.put(buffer.getInfo().id(), buffer);
    }

    public void removeBuffer(@IntRange(from = 0) int id) {
        buffers.remove(id);
    }

    public Buffer buffer(@IntRange(from = 0) int id) {
        return buffers.get(id);
    }

    public void updateBufferInfo(@NonNull BufferInfo bufferInfo) {
        Buffer buffer = buffer(bufferInfo.id());
        if (buffer == null) return;
        buffer.setInfo(bufferInfo);
    }

    public void init(List<BufferInfo> bufferInfos) {
        for (BufferInfo info : bufferInfos) {
            if (this.bufferInfos.get(objectName(info)) == null)
                this.bufferInfos.put(objectName(info), new HashSet<>());

            this.bufferInfos.get(objectName(info)).add(info);
        }
    }

    private String objectName(BufferInfo info) {
        if (info.type() == BufferInfo.Type.STATUS)
            return String.valueOf(info.networkId());
        else
            return info.networkId() + "/" + info.name();
    }

    public void postInit(String objectName, QIrcUser ircUser) {
        if (bufferInfos.get(objectName) != null)
            for (BufferInfo info : bufferInfos.get(objectName)) {
                QNetwork network = client.networkManager().network(info.networkId());
                assertNotNull(network);
                createBuffer(new QueryBuffer(info, ircUser));
            }
    }

    public void postInit(String objectName, QNetwork ircUser) {
        if (bufferInfos.get(objectName) != null)
            for (BufferInfo info : bufferInfos.get(objectName)) {
                QNetwork network = client.networkManager().network(info.networkId());
                assertNotNull(network);
                createBuffer(new StatusBuffer(info, ircUser));
            }
    }

    public Map<Integer, Buffer> buffers() {
        return buffers;
    }

    public void postInit(String objectName, QIrcChannel ircChannel) {
        if (bufferInfos.get(objectName) != null)
            for (BufferInfo info : bufferInfos.get(objectName)) {
                QNetwork network = client.networkManager().network(info.networkId());
                assertNotNull(network);
                createBuffer(new ChannelBuffer(info, ircChannel));
            }
    }
}
