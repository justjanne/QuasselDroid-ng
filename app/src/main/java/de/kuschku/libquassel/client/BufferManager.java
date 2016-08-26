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
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.events.BacklogInitEvent;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.localtypes.buffers.Buffers;
import de.kuschku.libquassel.localtypes.buffers.ChannelBuffer;
import de.kuschku.libquassel.localtypes.buffers.QueryBuffer;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.util.observables.lists.ObservableSet;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class BufferManager {
    @NonNull
    private final Map<Integer, Buffer> buffers = new HashMap<>();
    private final Client client;

    private final Map<String, Integer> buffersByNick = new HashMap<>();
    private final Map<String, Integer> buffersByChannel = new HashMap<>();
    private final Map<Integer, ObservableSet<Integer>> buffersByNetwork = new HashMap<>();
    private final ObservableSet<Integer> bufferIds = new ObservableSet<>();
    private final Set<Integer> laterRequests = new HashSet<>();

    public BufferManager(Client client) {
        this.client = client;
    }

    public void createBuffer(@NonNull Buffer buffer) {
        buffers.put(buffer.getInfo().id, buffer);
        bufferIds.add(buffer.getInfo().id);
        byNetwork(buffer.getInfo().networkId).add(buffer.getInfo().id);
        updateBufferMapEntries(buffer, buffer.getInfo().name);
    }

    public void removeBuffer(@IntRange(from = 0) int id) {
        Buffer buffer = buffers.get(id);
        if (buffer != null)
            byNetwork(buffer.getInfo().networkId).remove(id);
        buffers.remove(id);
        bufferIds.remove(id);
    }

    public Buffer buffer(@IntRange(from = 0) int id) {
        return buffers.get(id);
    }

    public void updateBufferInfo(@NonNull BufferInfo bufferInfo) {
        Buffer buffer = buffer(bufferInfo.id);
        if (buffer == null) return;
        if (buffer.getInfo().networkId != bufferInfo.networkId) {
            buffersByNetwork.get(buffer.getInfo().networkId).remove(bufferInfo.id);
            buffersByNetwork.get(buffer.getInfo().networkId).add(bufferInfo.id);
        }
        buffer.setInfo(bufferInfo);
    }

    public void init(@NonNull List<BufferInfo> bufferInfos) {
        for (BufferInfo info : bufferInfos) {
            createBuffer(info);
            laterRequests.add(info.id);
        }
    }

    @NonNull
    public Map<Integer, Buffer> buffers() {
        return buffers;
    }

    public void createBuffer(@NonNull BufferInfo info) {
        Buffer buffer = Buffers.fromType(info, client);
        assertNotNull(buffer);
        createBuffer(buffer);
    }

    public boolean exists(@NonNull BufferInfo info) {
        return buffers.containsKey(info.id);
    }

    public void renameBuffer(int bufferId, @NonNull String newName) {
        Buffer buffer = buffer(bufferId);
        if (buffer != null) {
            buffer.renameBuffer(newName);
        }
    }

    private void updateBufferMapEntries(@NonNull Buffer buffer, String name) {
        buffersByNick.remove(buffer.objectName());
        buffersByChannel.remove(buffer.objectName());
        if (buffer instanceof ChannelBuffer) {
            buffersByChannel.put(buffer.objectName(name), buffer.getInfo().id);
        } else if (buffer instanceof QueryBuffer) {
            buffersByNick.put(buffer.objectName(name), buffer.getInfo().id);
        }
    }

    @Nullable
    public ChannelBuffer channel(@Nullable QIrcChannel channel) {
        if (channel == null)
            return null;
        if (!buffersByChannel.containsKey(channel.getObjectName()))
            return null;
        Buffer buffer = buffer(buffersByChannel.get(channel.getObjectName()));
        if (!(buffer instanceof ChannelBuffer))
            return null;
        return (ChannelBuffer) buffer;
    }

    @Nullable
    public QueryBuffer user(@Nullable QIrcUser user) {
        if (user == null)
            return null;
        if (!buffersByNick.containsKey(user.getObjectName()))
            return null;
        Buffer buffer = buffer(buffersByNick.get(user.getObjectName()));
        if (!(buffer instanceof QueryBuffer))
            return null;
        return (QueryBuffer) buffer;
    }

    public ObservableSet<Integer> byNetwork(@IntRange(from = 0) int networkId) {
        if (!buffersByNetwork.containsKey(networkId))
            buffersByNetwork.put(networkId, new ObservableSet<>());
        return buffersByNetwork.get(networkId);
    }

    @NonNull
    public ObservableSet<Integer> bufferIds() {
        return bufferIds;
    }

    public void doBacklogInit(int amount) {
        BusProvider provider = client.provider();
        assertNotNull(provider);

        Set<Integer> visibleBuffers = new HashSet<>();
        for (QBufferViewConfig bufferConfig : client.bufferViewManager().bufferViewConfigs()) {
            visibleBuffers.addAll(bufferConfig.bufferIds());
        }
        laterRequests.retainAll(visibleBuffers);
        for (int id : laterRequests) {
            client.backlogManager().requestBacklogInitial(id, amount);
        }
        laterRequests.clear();
        int waitingMax = client.backlogManager().waitingMax();
        int waitingCurrently = client.backlogManager().waiting().size();

        provider.sendEvent(new BacklogInitEvent(waitingMax - waitingCurrently, waitingMax));

        if (waitingMax == 0) {
            // TODO: Send event to set up chat list
            client.setConnectionStatus(ConnectionChangeEvent.Status.CONNECTED);
        }
    }
}
