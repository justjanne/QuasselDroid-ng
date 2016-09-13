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
import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.BufferSyncerSerializer;
import de.kuschku.libquassel.syncables.types.abstracts.ABufferSyncer;
import de.kuschku.libquassel.syncables.types.interfaces.QBacklogManager;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferSyncer;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;
import de.kuschku.util.observables.lists.ObservableElement;
import de.kuschku.util.observables.lists.ObservableSet;
import de.kuschku.util.observables.lists.ObservableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class BufferSyncer extends ABufferSyncer {

    @NonNull
    private final SparseArray<ObservableElement<Integer>> activities = new SparseArray<>();
    @NonNull
    private SparseIntArray lastSeenMsgs = new SparseIntArray();
    @NonNull
    private SparseIntArray markerLines = new SparseIntArray();
    @NonNull
    private Map<Integer, ObservableSet<Message.Type>> filters = new HashMap<>();

    public BufferSyncer(@NonNull Map<Integer, Integer> lastSeenMsgs, @NonNull Map<Integer, Integer> markerLines) {
        assertNotNull(lastSeenMsgs);
        assertNotNull(markerLines);

        for (int bufferId : lastSeenMsgs.keySet()) {
            this.lastSeenMsgs.put(bufferId, lastSeenMsgs.get(bufferId));
        }
        for (int bufferId : markerLines.keySet()) {
            this.markerLines.put(bufferId, markerLines.get(bufferId));
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
        assertNotNull(client);
        QBacklogManager backlogManager = client.backlogManager();
        assertNotNull(backlogManager);

        if (msgId < 0)
            return;

        int oldLastSeenMsg = lastSeenMsg(buffer);
        if (oldLastSeenMsg < msgId) {
            lastSeenMsgs.put(buffer, msgId);
        }
        setActivity(buffer, 0);
        ObservableComparableSortedList<Message> filtered = backlogManager.filtered(buffer);
        for (Message m : filtered) {
            addActivity(m);
        }
        client.bufferManager().bufferIds().notifyItemChanged(buffer);
        _update();
    }

    @Override
    public void _setMarkerLine(int buffer, int msgId) {
        if (msgId < 0)
            return;

        int oldMarkerline = markerLine(buffer);
        if (oldMarkerline < msgId) {
            markerLines.put(buffer, msgId);
            client.backlogStorage().setMarkerLine(buffer, msgId);
        }
        client.bufferManager().bufferIds().notifyItemChanged(buffer);
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
        assertNotNull(client);

        for (QBufferViewConfig config : client.bufferViewManager().bufferViewConfigs()) {
            config.deleteBuffer(buffer);
        }
        this.filters.remove(buffer);
        markerLines.delete(buffer);
        lastSeenMsgs.delete(buffer);
        client.bufferManager().removeBuffer(buffer);
        _update();
    }

    @Override
    public void _requestRenameBuffer(int buffer, String newName) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _renameBuffer(int bufferId, @NonNull String newName) {
        assertNotNull(client);

        client.bufferManager().renameBuffer(bufferId, newName);
        _update();
    }

    @Override
    public void _requestMergeBuffersPermanently(int buffer1, int buffer2) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _mergeBuffersPermanently(int buffer1, int buffer2) {
        client.backlogStorage().merge(buffer1, buffer2);
        _removeBuffer(buffer2);
    }

    @Override
    public void _requestPurgeBufferIds() {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _requestMarkBufferAsRead(int buffer) {
        assertNotNull(client);

        int lastMessage = client.backlogStorage().getLatest(buffer);
        if (lastMessage != -1) {
            requestSetLastSeenMsg(buffer, lastMessage);
        }
    }

    @Override
    public void _markBufferAsRead(int buffer) {
        assertNotNull(client);

        ObservableSortedList<Message> messages = client.backlogStorage().getUnfiltered(buffer);
        Message lastMessage = messages.last();
        if (messages.isEmpty() || lastMessage == null) {
            _setLastSeenMsg(buffer, -1);
            _setMarkerLine(buffer, -1);
        } else {
            _setLastSeenMsg(buffer, lastMessage.id);
            _setMarkerLine(buffer, lastMessage.id);
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
    public void _update(@NonNull QBufferSyncer from) {
        lastSeenMsgs = from.lastSeenMsgs();
        markerLines = from.markerLines();
        _update();
    }

    public ObservableElement<Integer> activity(int bufferid) {
        assertNotNull(activities);
        ensureExistingActivity(bufferid);

        return activities.get(bufferid);
    }

    public void setActivity(int bufferid, int activity) {
        assertNotNull(activities);
        ensureExistingActivity(bufferid);

        activities.get(bufferid).set(activity);
    }

    private void ensureExistingActivity(int bufferid) {
        if (activities.get(bufferid) == null)
            activities.put(bufferid, new ObservableElement<>(0));
    }

    public void addActivity(int bufferid, int activity) {
        assertNotNull(activities);
        ensureExistingActivity(bufferid);

        activities.get(bufferid).set(activities.get(bufferid).get() | activity);
    }

    public void addActivity(int bufferid, @NonNull Message.Type type) {
        ensureExistingActivity(bufferid);
        addActivity(bufferid, type.value);
    }

    public void addActivity(@NonNull Message message) {
        int bufferId = message.bufferInfo.id;
        int lastSeenMsg = lastSeenMsg(bufferId);
        boolean filtered = getFilteredTypes(bufferId).contains(message.type);
        if (!filtered && message.id > lastSeenMsg) {
            addActivity(bufferId, message.type);
        }
    }

    @Override
    public SparseIntArray lastSeenMsgs() {
        return lastSeenMsgs;
    }

    @Override
    public SparseIntArray markerLines() {
        return markerLines;
    }

    @Override
    public ObservableSet<Message.Type> getFilteredTypes(int bufferId) {
        if (!this.filters.containsKey(bufferId)) {
            this.filters.put(bufferId, new ObservableSet<>());
            setFilters(bufferId, client.metaDataManager().hiddendata(client.coreId(), bufferId));
        }
        return this.filters.get(bufferId);
    }

    @Override
    public int getFilters(int bufferId) {
        int filters = 0x00000000;
        for (Message.Type type : getFilteredTypes(bufferId)) {
            filters |= type.value;
        }
        return filters;
    }

    @Override
    public void setFilters(int bufferId, int filters) {
        client.metaDataManager().setHiddendata(client.coreId(), bufferId, filters);
        for (Message.Type type : Message.Type.values()) {
            if ((filters & type.value) != 0) {
                getFilteredTypes(bufferId).add(type);
            } else {
                getFilteredTypes(bufferId).remove(type);
            }
        }
    }
}
