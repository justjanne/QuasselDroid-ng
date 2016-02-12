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

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.events.BacklogInitEvent;
import de.kuschku.libquassel.events.BacklogReceivedEvent;
import de.kuschku.libquassel.events.BufferChangeEvent;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
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
    private final Set<Integer> initialized = new HashSet<>();
    @NonNull
    private final Set<Integer> waiting = new HashSet<>();
    private int waitingMax = 0;
    @IntRange(from = -1)
    private int openBuffer;

    public BacklogManager(Client client, BacklogStorage storage) {
        this.client = client;
        this.storage = storage;
    }

    @Override
    public void requestMoreBacklog(int bufferId, int amount) {
        Message last;
        if (!initialized.contains(bufferId) || null == (last = storage.getUnfiltered(bufferId).last()))
            requestBacklogInitial(bufferId, amount);
        else {
            requestBacklog(bufferId, -1, last.messageId, amount, 0);
        }
    }

    @Override
    public void requestBacklogInitial(int id, int amount) {
        if (waiting.contains(id) || initialized.contains(id))
            return;

        waiting.add(id);
        waitingMax++;
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
        if (messages.size() > 0 && !client.bufferManager().exists(messages.get(0).bufferInfo))
            client.bufferManager().createBuffer(messages.get(0).bufferInfo);
        provider.sendEvent(new BacklogReceivedEvent(id));
        if (id == openBuffer && openBuffer != -1)
            client.bufferSyncer().requestMarkBufferAsRead(openBuffer);
        removeWaiting(id);
    }

    private void removeWaiting(int id) {
        waiting.remove(id);
        initialized.add(id);
        checkWaiting();
    }

    private void checkWaiting() {
        assertNotNull(provider);

        if (client.connectionStatus() == ConnectionChangeEvent.Status.LOADING_BACKLOG) {
            provider.event.postSticky(new BacklogInitEvent(waitingMax - waiting.size(), waitingMax));
            if (waiting.isEmpty()) {
                client.setConnectionStatus(ConnectionChangeEvent.Status.CONNECTED);
            }
        }
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
            if (id == openBuffer && openBuffer != -1)
                client.bufferSyncer().requestMarkBufferAsRead(openBuffer);
            waiting.remove(id);
            initialized.add(id);
        }
        checkWaiting();
    }

    @NonNull
    @Override
    public BacklogFilter filter(int id) {
        return storage.getFilter(id);
    }

    @NonNull
    @Override
    public ObservableComparableSortedList<Message> unfiltered(int id) {
        return storage.getUnfiltered(id);
    }

    @NonNull
    @Override
    public ObservableComparableSortedList<Message> filtered(int id) {
        return storage.getFiltered(id);
    }

    @Override
    public void open(int bufferId) {
        assertNotNull(provider);

        openBuffer = bufferId;
        if (bufferId != -1)
            client.bufferSyncer().requestMarkBufferAsRead(bufferId);
        provider.event.postSticky(new BufferChangeEvent());
    }

    @Override
    public int open() {
        return openBuffer;
    }

    @Override
    public void receiveBacklog(@NonNull Message msg) {
        storage.insertMessages(msg);
        if (msg.bufferInfo.id() == openBuffer && openBuffer != -1)
            client.bufferSyncer().requestMarkBufferAsRead(openBuffer);
    }

    @Override
    public int waitingMax() {
        return waitingMax;
    }

    @NonNull
    @Override
    public Set<Integer> waiting() {
        return waiting;
    }

    @Override
    public void _update(Map<String, QVariant> from) {

    }

    @Override
    public void _update(BacklogManager from) {

    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        super.init(objectName, provider, client);
    }
}
