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
import android.support.annotation.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.events.BacklogReceivedEvent;
import de.kuschku.libquassel.localtypes.BacklogFilter;
import de.kuschku.libquassel.localtypes.backlogstorage.BacklogStorage;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.abstracts.ABacklogManager;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class BacklogManager extends ABacklogManager<BacklogManager> {
    private final Client client;
    private final BacklogStorage storage;

    public BacklogManager(Client client, BacklogStorage storage) {
        this.client = client;
        this.storage = storage;
    }

    @Override
    public void requestMoreBacklog(int bufferId, int amount) {
        Message last;
        if (storage.getUnfiltered(bufferId).isEmpty() || null == (last = storage.getUnfiltered(bufferId).last()))
            requestBacklogInitial(bufferId, amount);
        else {
            requestBacklog(bufferId, -1, last.messageId, amount, 0);
        }
    }

    @Override
    public void requestBacklogInitial(int id, int amount) {
        requestBacklog(id, -1, -1, amount, 0);
    }

    @Override
    public void _requestBacklog(int id, int first, int last, int limit, int additional) {
        // Do nothing, we are on the client
    }

    @Override
    public void _receiveBacklog(int id, int first, int last, int limit, int additional, @NonNull List<Message> messages) {
        assertNotNull(provider);

        storage.insertMessages(id, messages.toArray(new Message[messages.size()]));
        client.initBacklog(id);
        provider.sendEvent(new BacklogReceivedEvent(id));
    }

    @Override
    public void _requestBacklogAll(int first, int last, int limit, int additional) {
        // Do nothing, we are on the client
    }

    @Override
    public void _receiveBacklogAll(int first, int last, int limit, int additional, @NonNull List<Message> messages) {
        assertNotNull(provider);

        Set<Integer> buffers = new HashSet<>();
        for (Message message : messages) {
            storage.insertMessages(message.bufferInfo.id(), message);
            buffers.add(message.bufferInfo.id());
        }
        for (int id : buffers) {
            provider.sendEvent(new BacklogReceivedEvent(id));
        }
    }

    @Nullable
    @Override
    public BacklogFilter filter(int id) {
        return storage.getFilter(id);
    }

    @Nullable
    @Override
    public ObservableComparableSortedList<Message> unfiltered(int id) {
        return storage.getUnfiltered(id);
    }

    @Nullable
    @Override
    public ObservableComparableSortedList<Message> filtered(int id) {
        return storage.getFiltered(id);
    }

    @Override
    public void update(Map<String, QVariant> from) {

    }

    @Override
    public void update(BacklogManager from) {

    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        super.init(objectName, provider, client);
    }
}
