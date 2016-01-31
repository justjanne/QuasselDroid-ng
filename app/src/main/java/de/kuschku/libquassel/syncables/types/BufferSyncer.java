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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.syncables.types;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.SparseIntArray;

import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.BufferSyncerSerializer;
import de.kuschku.util.observables.lists.ObservableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class BufferSyncer extends SyncableObject<BufferSyncer> {
    @NonNull
    private SparseIntArray LastSeenMsg = new SparseIntArray();
    @NonNull
    private SparseIntArray MarkerLines = new SparseIntArray();

    private Client client;

    public BufferSyncer(@NonNull Map<Integer, Integer> LastSeenMsg, @NonNull Map<Integer, Integer> MarkerLines) {
        for (Map.Entry<Integer, Integer> entry : LastSeenMsg.entrySet()) {
            this.LastSeenMsg.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : MarkerLines.entrySet()) {
            this.MarkerLines.put(entry.getKey(), entry.getValue());
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {
        client.setBufferSyncer(this);
        setClient(client);
    }

    @Override
    public void update(@NonNull BufferSyncer from) {
        LastSeenMsg = from.LastSeenMsg;
        MarkerLines = from.MarkerLines;
    }

    @Override
    public void update(@NonNull Map<String, QVariant> from) {
        update(BufferSyncerSerializer.get().fromDatastream(from));
    }

    public void markBufferAsRead(@IntRange(from = 0) int bufferId) {
        ObservableSortedList<Message> buffer = client.getBacklogManager().get(bufferId);
        assertNotNull(buffer);

        Message last = buffer.last();
        assertNotNull(last);

        int messageId = last.messageId;

        setLastSeenMsg(bufferId, messageId);
        setMarkerLine(bufferId, messageId);
    }

    public void setLastSeenMsg(int bufferId, int msgId) {
        LastSeenMsg.put(bufferId, msgId);
    }

    public void setMarkerLine(int bufferId, int msgId) {
        MarkerLines.put(bufferId, msgId);
    }

    public int getLastSeenMsg(int bufferId) {
        return LastSeenMsg.get(bufferId, -1);
    }

    public int getMarkerLine(int bufferId) {
        return MarkerLines.get(bufferId, -1);
    }

    public void removeBuffer(int bufferId) {
        LastSeenMsg.put(bufferId, -1);
        MarkerLines.put(bufferId, -1);
    }
}
