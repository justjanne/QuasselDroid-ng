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

package de.kuschku.libquassel.localtypes.backlogmanagers;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.events.BacklogReceivedEvent;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.util.observables.AutoScroller;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;
import de.kuschku.util.observables.lists.ObservableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class SimpleBacklogManager extends BacklogManager<SimpleBacklogManager> {
    @NonNull
    private final SparseArray<ObservableSortedList<Message>> backlogs = new SparseArray<>();
    @NonNull
    private final SparseArray<ObservableSortedList<Message>> filteredBacklogs = new SparseArray<>();
    @NonNull
    private final SparseArray<BacklogFilter> filters = new SparseArray<>();
    @NonNull
    private final BusProvider busProvider;
    @Nullable
    private Client client;

    public SimpleBacklogManager(@NonNull BusProvider busProvider) {
        this.busProvider = busProvider;
    }

    public void setClient(@Nullable Client client) {
        this.client = client;
    }

    public void requestBacklog(int bufferId, int from, int to, int count, int extra) {
        busProvider.dispatch(new SyncFunction<>("BacklogManager", "", "requestBacklog", Lists.<QVariant>newArrayList(
                new QVariant<>("BufferId", bufferId),
                new QVariant<>("MsgId", from),
                new QVariant<>("MsgId", to),
                new QVariant<>(count),
                new QVariant<>(extra)
        )));
    }

    public void receiveBacklog(@IntRange(from = 0) int bufferId, int from, int to, int count, int extra, @NonNull List<Message> messages) {
        assertNotNull(client);
        get(bufferId).addAll(messages);
        client.getNotificationManager().receiveMessages(messages);

        busProvider.sendEvent(new BacklogReceivedEvent(bufferId));
    }

    @Override
    public void displayMessage(@IntRange(from = 0) int bufferId, @NonNull Message message) {
        assertNotNull(client);
        ObservableSortedList<Message> messages = get(bufferId);
        assertNotNull(messages);

        messages.add(message);
        client.getNotificationManager().receiveMessage(message);
    }

    public void bind(@IntRange(from = 0) int bufferId, @NonNull RecyclerView.Adapter adapter, @Nullable AutoScroller scroller) {
        ObservableSortedList<Message> messages = get(bufferId);
        assertNotNull(messages);

        messages.addCallback(new AdapterUICallbackWrapper(adapter, scroller));
    }

    @Override
    public void requestMoreBacklog(@IntRange(from = 0) int bufferId, int count) {
        ObservableSortedList<Message> backlog = backlogs.get(bufferId);
        int messageId = (backlog == null || backlog.last() == null) ? -1 : backlog.last().messageId;

        requestBacklog(bufferId, -1, messageId, count, 0);
    }

    public ObservableSortedList<Message> get(@IntRange(from = -1) int bufferId) {
        ensureExisting(bufferId);

        return backlogs.get(bufferId);
    }

    public ObservableSortedList<Message> getFiltered(@IntRange(from = -1) int bufferId) {
        ensureExisting(bufferId);

        return filteredBacklogs.get(bufferId);
    }

    public BacklogFilter getFilter(@IntRange(from = -1) int bufferId) {
        ensureExisting(bufferId);

        return filters.get(bufferId);
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

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {

    }

    @Override
    public void update(SimpleBacklogManager from) {

    }

    @Override
    public void update(Map<String, QVariant> from) {

    }
}
