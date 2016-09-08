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

package de.kuschku.libquassel.localtypes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class BacklogFilter implements UICallback {
    @NonNull
    private final Client client;
    private final int bufferId;
    @NonNull
    private final ObservableComparableSortedList<Message> unfiltered;
    @NonNull
    private final ObservableComparableSortedList<Message> filtered;

    private final EventBus bus = new EventBus();
    @Nullable
    private CharSequence searchQuery;

    private Message markerlineMessage;

    public BacklogFilter(@NonNull Client client, int bufferId, @NonNull ObservableComparableSortedList<Message> unfiltered, @NonNull ObservableComparableSortedList<Message> filtered) {
        this.client = client;
        this.bufferId = bufferId;
        this.unfiltered = unfiltered;
        this.filtered = filtered;
        this.bus.register(this);
        client.bufferSyncer().getFilteredTypes(bufferId).addCallback(new ElementCallback<Message.Type>() {
            @Override
            public void notifyItemInserted(Message.Type element) {
                bus.post(new UpdateRemoveEvent());
            }

            @Override
            public void notifyItemRemoved(Message.Type element) {
                bus.post(new UpdateAddEvent());
            }

            @Override
            public void notifyItemChanged(Message.Type element) {

            }
        });
        updateDayChangeMessages();
    }

    private Message createMarkerlineMessage(int id) {
        return Message.create(
                id,
                null,
                Message.Type.Markerline,
                new Message.Flags((byte) 0x00),
                BufferInfo.create(
                        bufferId,
                        -1,
                        BufferInfo.Type.INVALID,
                        -1,
                        null
                ),
                null,
                null
        );
    }

    public void setMarkerlineMessage(int id) {
        /*
        Message markerlineMessage = this.markerlineMessage;
        bus.post(new MessageRemoveEvent(markerlineMessage));
        this.markerlineMessage = createMarkerlineMessage(id);
        bus.post(new MessageInsertEvent(this.markerlineMessage));
        */
    }

    @Override
    public void notifyItemInserted(int position) {
        Message message = unfiltered.get(position);
        bus.post(new MessageFilterEvent(message));
    }

    private void updateDayChangeMessages() {
        /*
        LocalDate date = null;
        Message lastMessage = null;
        for (Message message : filtered) {
            if (Objects.equals(date, message.getLocalDate()))
                continue;
            date = message.getLocalDate();
            if (message.type == Message.Type.DayChange) {
                if (lastMessage != null && lastMessage.type == Message.Type.DayChange) {
                    bus.post(new MessageRemoveEvent(lastMessage));
                }
                lastMessage = message;
                continue;
            }

            lastMessage = message;
            date = message.getLocalDate();
            DateTime time = message.time.withMillisOfDay(0);
            bus.post(new MessageInsertEvent(Message.create(
                    (int) DateTimeUtils.toJulianDay(time.getMillis()),
                    time,
                    Message.Type.DayChange,
                    new Message.Flags(false, false, false, false, false),
                    BufferInfo.create(
                            bufferId,
                            -1,
                            BufferInfo.Type.INVALID,
                            -1,
                            null
                    ),
                    "",
                    ""
            )));
        }
        DateTime time = DateTime.now();
        if (!Objects.equals(date, time.toLocalDate())) {
            time = time.withMillisOfDay(0);
            bus.post(new MessageInsertEvent(Message.create(
                    (int) DateTimeUtils.toJulianDay(time.getMillis()),
                    time,
                    Message.Type.DayChange,
                    new Message.Flags(false, false, false, false, false),
                    BufferInfo.create(
                            bufferId,
                            -1,
                            BufferInfo.Type.INVALID,
                            -1,
                            null
                    ),
                    "",
                    ""
            )));
        }
        */
    }

    private boolean filterItem(@NonNull Message message) {
        QNetwork network = client.networkManager().network(client.bufferManager().buffer(message.bufferInfo.id).getInfo().networkId);
        assertNotNull(network);
        boolean ignored = client.ignoreListManager() != null && client.ignoreListManager().matches(message, network);
        boolean filtered = client.bufferSyncer().getFilteredTypes(bufferId).contains(message.type);
        boolean isSearching = searchQuery != null && searchQuery.length() != 0;
        return ignored || filtered || (isSearching && !message.content.contains(searchQuery));
    }

    public void update() {
        bus.post(new UpdateAddEvent());
        bus.post(new UpdateRemoveEvent());
    }

    public void setQuery(CharSequence query) {
        searchQuery = query;
        update();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(UpdateAddEvent event) {
        for (Message message : unfiltered) {
            if (!filterItem(message)) {
                bus.post(new MessageInsertEvent(message));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(UpdateRemoveEvent event) {
        for (Message message : unfiltered) {
            if (filterItem(message)) {
                bus.post(new MessageRemoveEvent(message));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(@NonNull MessageFilterEvent event) {
        if (!filterItem(event.msg)) bus.post(new MessageInsertEvent(event.msg));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull MessageInsertEvent event) {
        filtered.add(event.msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@NonNull MessageRemoveEvent event) {
        filtered.remove(event.msg);
    }

    @Override
    public void notifyItemChanged(int position) {
        filtered.notifyItemChanged(position);
    }

    @Override
    public void notifyItemRemoved(int position) {
        filtered.remove(position);
    }

    @Override
    public void notifyItemMoved(int from, int to) {
        // Can’t occur: Sorted List
    }

    @Override
    public void notifyItemRangeInserted(int position, int count) {
        for (int i = position; i < position + count; i++) {
            notifyItemInserted(i);
        }
    }

    @Override
    public void notifyItemRangeChanged(int position, int count) {
        for (int i = position; i < position + count; i++) {
            notifyItemChanged(i);
        }
    }

    @Override
    public void notifyItemRangeRemoved(int position, int count) {
        for (int i = position; i < position + count; i++) {
            notifyItemRemoved(i);
        }
    }

    private class MessageInsertEvent {
        public final Message msg;

        public MessageInsertEvent(Message msg) {
            this.msg = msg;
        }
    }

    private class MessageRemoveEvent {
        public final Message msg;

        public MessageRemoveEvent(Message msg) {
            this.msg = msg;
        }
    }

    private class MessageFilterEvent {
        public final Message msg;

        public MessageFilterEvent(Message msg) {
            this.msg = msg;
        }
    }

    private class UpdateAddEvent {
    }

    private class UpdateRemoveEvent {
    }
}
