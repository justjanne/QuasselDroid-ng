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

package de.kuschku.libquassel.syncables.types.impl;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseIntArray;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.BufferSyncerSerializer;
import de.kuschku.libquassel.syncables.types.abstracts.ABufferSyncer;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;
import de.kuschku.util.observables.lists.ObservableSortedList;

public class BufferSyncer extends ABufferSyncer<BufferSyncer> {

    private SparseIntArray lastSeenMsgs = new SparseIntArray();
    private SparseIntArray markerLines = new SparseIntArray();
    private SparseIntArray activities = new SparseIntArray();
    private Set<Integer> bufferIds = new HashSet<>();

    public BufferSyncer(@NonNull Map<Integer, Integer> lastSeenMsgs, @NonNull Map<Integer, Integer> markerLines) {
        for (int bufferId : lastSeenMsgs.keySet()) {
            this.lastSeenMsgs.put(bufferId, lastSeenMsgs.get(bufferId));
            this.bufferIds.add(bufferId);
        }
        for (int bufferId : markerLines.keySet()) {
            this.markerLines.put(bufferId, markerLines.get(bufferId));
            this.bufferIds.add(bufferId);
        }
    }

    @Override
    public int lastSeenMsg(int buffer) {
        return lastSeenMsgs.get(buffer, -1);
    }

    @Override
    public int markerLine(int buffer) {
        return markerLines.get(buffer, -1);
    }

    @Override
    public void _setLastSeenMsg(int buffer, int msgId) {
        if (msgId < 0)
            return;

        int oldLastSeenMsg = lastSeenMsg(buffer);
        if (oldLastSeenMsg < msgId) {
            lastSeenMsgs.put(buffer, msgId);
        }
        setActivity(buffer, 0);
        ObservableComparableSortedList<Message> filtered = client.backlogManager().filtered(buffer);
        for (Message m : filtered) {
            addActivity(m);
        }
        _update();
    }

    @Override
    public void _setMarkerLine(int buffer, int msgId) {
        if (msgId < 0)
            return;

        int oldLastSeenMsg = markerLine(buffer);
        if (oldLastSeenMsg < msgId) {
            markerLines.put(buffer, msgId);
        }
        _update();
    }

    @Override
    public void _requestSetLastSeenMsg(int buffer, int msgId) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _requestSetMarkerLine(int buffer, int msgId) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _requestRemoveBuffer(int buffer) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _removeBuffer(int buffer) {
        markerLines.removeAt(markerLines.indexOfKey(buffer));
        lastSeenMsgs.removeAt(lastSeenMsgs.indexOfKey(buffer));
        bufferIds.remove(buffer);
        client.bufferManager().removeBuffer(buffer);
        _update();
    }

    @Override
    public void _requestRenameBuffer(int buffer, String newName) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _renameBuffer(int bufferId, @NonNull String newName) {
        client.bufferManager().renameBuffer(bufferId, newName);
        _update();
    }

    @Override
    public void _requestMergeBuffersPermanently(int buffer1, int buffer2) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _mergeBuffersPermanently(int buffer1, int buffer2) {
        _removeBuffer(buffer2);
    }

    @Override
    public void _requestPurgeBufferIds() {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _requestMarkBufferAsRead(int buffer) {
        int lastMessage = client.backlogStorage().getLatest(buffer);
        if (lastMessage != -1) {
            requestSetLastSeenMsg(buffer, lastMessage);
            requestSetMarkerLine(buffer, lastMessage);
        }
    }

    @Override
    public void _markBufferAsRead(int buffer) {
        ObservableSortedList<Message> messages = client.backlogStorage().getUnfiltered(buffer);
        Message lastMessage = messages.last();
        if (messages.isEmpty() || lastMessage == null) {
            _setLastSeenMsg(buffer, -1);
            _setMarkerLine(buffer, -1);
        } else {
            _setLastSeenMsg(buffer, lastMessage.messageId);
            _setMarkerLine(buffer, lastMessage.messageId);
        }
    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        super.init(objectName, provider, client);
        client.setBufferSyncer(this);
    }

    @Override
    public void _update(@NonNull Map<String, QVariant> from) {
        _update(BufferSyncerSerializer.get().fromLegacy(from));
    }

    @Override
    public void _update(@NonNull BufferSyncer from) {
        lastSeenMsgs = from.lastSeenMsgs;
        markerLines = from.markerLines;
        _update();
    }

    public int activity(int bufferid) {
        return activities.get(bufferid, 0);
    }

    public void setActivity(int bufferid, int activity) {
        activities.put(bufferid, activity);
    }

    public void addActivity(int bufferid, int activity) {
        activities.put(bufferid, activities.get(bufferid) | activity);
    }

    public void addActivity(int bufferid, Message.Type type) {
        addActivity(bufferid, type.value);
    }

    public void addActivity(Message message) {
        int lastSeenMsg = lastSeenMsg(message.bufferInfo.id());
        if (message.messageId > lastSeenMsg) {
            addActivity(message.bufferInfo.id(), message.type);
            Log.d("libquassel", "Unread: " + lastSeenMsg + "/" + message);
        }
    }
}
