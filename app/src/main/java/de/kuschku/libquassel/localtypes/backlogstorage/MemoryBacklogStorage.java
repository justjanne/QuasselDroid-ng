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

package de.kuschku.libquassel.localtypes.backlogstorage;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.localtypes.BacklogFilter;
import de.kuschku.libquassel.message.Message;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class MemoryBacklogStorage implements BacklogStorage {
    @NonNull
    private final SparseArray<ObservableComparableSortedList<Message>> backlogs = new SparseArray<>();
    @NonNull
    private final SparseArray<ObservableComparableSortedList<Message>> filteredBacklogs = new SparseArray<>();
    @NonNull
    private final SparseArray<BacklogFilter> filters = new SparseArray<>();
    @NonNull
    private final SparseArray<Integer> latestMessage = new SparseArray<>();

    private Client client;

    @NonNull
    @Override
    public ObservableComparableSortedList<Message> getUnfiltered(@IntRange(from = -1) int bufferid) {
        ensureExisting(bufferid);
        return backlogs.get(bufferid);
    }

    @NonNull
    @Override
    public ObservableComparableSortedList<Message> getFiltered(@IntRange(from = -1) int bufferid) {
        ensureExisting(bufferid);
        return filteredBacklogs.get(bufferid);
    }

    @NonNull
    @Override
    public BacklogFilter getFilter(int bufferid) {
        ensureExisting(bufferid);
        return filters.get(bufferid);
    }

    @Override
    public int getLatest(@IntRange(from = 0) int bufferid) {
        return latestMessage.get(bufferid, -1);
    }

    @Override
    public void insertMessages(@IntRange(from = 0) int bufferId, @NonNull Message... messages) {
        ensureExisting(bufferId);
        for (Message message : messages) {
            client.unbufferBuffer(message.bufferInfo);
            backlogs.get(bufferId).add(message);
            updateLatest(message);
        }
    }

    public void updateLatest(@NonNull Message message) {
        if (message.messageId > getLatest(message.bufferInfo.id())) {
            latestMessage.put(message.bufferInfo.id(), message.messageId);
        }
    }

    @Override
    public void insertMessages(@NonNull Message... messages) {
        for (Message message : messages) {
            ensureExisting(message.bufferInfo.id());
            client.unbufferBuffer(message.bufferInfo);
            backlogs.get(message.bufferInfo.id()).add(message);
            updateLatest(message);
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private void ensureExisting(@IntRange(from = -1) int bufferId) {
        assertNotNull(client);
        if (backlogs.get(bufferId) == null) {
            ObservableComparableSortedList<Message> messages = new ObservableComparableSortedList<>(Message.class, true);
            ObservableComparableSortedList<Message> filteredMessages = new ObservableComparableSortedList<>(Message.class, true);
            BacklogFilter backlogFilter = new BacklogFilter(client, bufferId, messages, filteredMessages);
            messages.addCallback(backlogFilter);
            backlogs.put(bufferId, messages);
            filteredBacklogs.put(bufferId, filteredMessages);
            filters.put(bufferId, backlogFilter);
        }
    }
}
