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
import android.util.Log;
import android.util.SparseArray;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.localtypes.BacklogFilter;
import de.kuschku.libquassel.localtypes.orm.ConnectedDatabase;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.message.Message_Table;
import de.kuschku.util.observables.lists.AndroidObservableComparableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class HybridBacklogStorage implements BacklogStorage {
    @NonNull
    private final SparseArray<AndroidObservableComparableSortedList<Message>> backlogs = new SparseArray<>();
    @NonNull
    private final SparseArray<AndroidObservableComparableSortedList<Message>> filteredBacklogs = new SparseArray<>();
    @NonNull
    private final SparseArray<BacklogFilter> filters = new SparseArray<>();
    @NonNull
    private final SparseArray<Integer> latestMessage = new SparseArray<>();
    @NonNull
    private final Set<BacklogFilter> filterSet = new HashSet<>();

    private ExecutorService executor = Executors.newCachedThreadPool();

    private Client client;

    @NonNull
    @Override
    public AndroidObservableComparableSortedList<Message> getUnfiltered(@IntRange(from = -1) int bufferid) {
        ensureExisting(bufferid);
        return backlogs.get(bufferid);
    }

    @NonNull
    @Override
    public AndroidObservableComparableSortedList<Message> getFiltered(@IntRange(from = -1) int bufferid) {
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
        FlowManager.getDatabase(ConnectedDatabase.class).executeTransaction(databaseWrapper -> {
            for (Message message : messages) {
                client.unbufferBuffer(message.bufferInfo);
                synchronized (backlogs) {
                    client.bufferSyncer().addActivity(message);
                    message.save();
                    message.bufferInfo.save();
                }
                updateLatest(message);
            }

            synchronized (backlogs) {
                if (backlogs.get(bufferId) != null)
                    backlogs.get(bufferId).addAll(Arrays.asList(messages));
            }
        });
    }

    @Override
    public void insertMessages(@IntRange(from = 0) int bufferId, List<Message> messages) {
        FlowManager.getDatabase(ConnectedDatabase.class).executeTransaction(databaseWrapper -> {
            for (Message message : messages) {
                client.unbufferBuffer(message.bufferInfo);
                synchronized (backlogs) {
                    if (backlogs.get(bufferId) != null)
                        backlogs.get(bufferId).add(message);
                    client.bufferSyncer().addActivity(message);
                    message.save();
                    message.bufferInfo.save();
                }
                updateLatest(message);
            }

            synchronized (backlogs) {
                if (backlogs.get(bufferId) != null)
                    backlogs.get(bufferId).addAll(messages);
            }
        });
    }

    public void updateLatest(@NonNull Message message) {
        if (message.id > getLatest(message.bufferInfo.id)) {
            latestMessage.put(message.bufferInfo.id, message.id);
        }
    }

    @Override
    public void insertMessages(@NonNull Message... messages) {
        FlowManager.getDatabase(ConnectedDatabase.class).executeTransaction(databaseWrapper -> {
            for (Message message : messages) {
                client.unbufferBuffer(message.bufferInfo);
                synchronized (backlogs) {
                    if (backlogs.get(message.bufferInfo.id) != null)
                        backlogs.get(message.bufferInfo.id).add(message);
                    if (client.bufferSyncer() != null)
                        client.bufferSyncer().addActivity(message);
                    message.save();
                    message.bufferInfo.save();
                }
                updateLatest(message);
            }
        });
    }

    @Override
    public void insertMessages(List<Message> messages) {
        FlowManager.getDatabase(ConnectedDatabase.class).executeTransaction(databaseWrapper -> {
            for (Message message : messages) {
                client.unbufferBuffer(message.bufferInfo);
                synchronized (backlogs) {
                    if (backlogs.get(message.bufferInfo.id) != null)
                        backlogs.get(message.bufferInfo.id).add(message);
                    client.bufferSyncer().addActivity(message);
                    message.save();
                    message.bufferInfo.save();
                }
                updateLatest(message);
            }
        });
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void markBufferUnused(@IntRange(from = 0) int bufferid) {
        synchronized (backlogs) {
            BacklogFilter filter = filters.get(bufferid);
            if (backlogs.get(bufferid) != null && filter != null)
                backlogs.get(bufferid).removeCallbacks();
            backlogs.remove(bufferid);
            if (filter != null)
                filter.onDestroy();
            if (filteredBacklogs.get(bufferid) != null)
                filteredBacklogs.get(bufferid).removeCallbacks();
            filteredBacklogs.delete(bufferid);
            synchronized (filterSet) {
                filterSet.remove(filter);
                filters.delete(bufferid);
            }
        }
    }

    @Override
    public void clear(@IntRange(from = 0) int bufferid) {
        synchronized (backlogs) {
            Log.w("libquassel", String.format("Backlog gap detected, clearing backlog for buffer %d", bufferid));
            SQLite.delete().from(Message.class).where(Message_Table.bufferInfo_id.eq(bufferid)).execute();
        }
    }

    @NonNull
    @Override
    public Set<BacklogFilter> getFilters() {
        return filterSet;
    }

    @Override
    public void setMarkerLine(@IntRange(from = 0) int buffer, int msgId) {
        BacklogFilter filter = filters.get(buffer);
        if (filter != null) {
            Log.w("DEBUG", "Setting markerline for open buffer");
            filter.setMarkerlineMessage(msgId);
        } else {
            Log.w("DEBUG", "Buffer not open");
        }
    }

    @Override
    public void merge(@IntRange(from = 0) int buffer1, @IntRange(from = 0) int buffer2) {
        SQLite.update(Message.class).set(Message_Table.bufferInfo_id.eq(buffer1)).where(Message_Table.bufferInfo_id.eq(buffer2)).execute();
    }

    private void ensureExisting(@IntRange(from = -1) int bufferId) {
        assertNotNull(client);
        if (backlogs.get(bufferId) == null) {
            AndroidObservableComparableSortedList<Message> messages = new AndroidObservableComparableSortedList<>(Message.class, true);
            AndroidObservableComparableSortedList<Message> filteredMessages = new AndroidObservableComparableSortedList<>(Message.class, true);
            BacklogFilter backlogFilter = new BacklogFilter(client, bufferId, messages, filteredMessages);
            if (client.bufferSyncer() != null) {
                backlogFilter.setMarkerlineMessage(client.bufferSyncer().markerLine(bufferId));
                Log.w("DEBUG", "Setting markerline for newly opened buffer");
            } else {
                Log.w("DEBUG", "BufferSyncer is null!");
            }
            messages.addCallback(backlogFilter);
            synchronized (backlogs) {
                backlogs.put(bufferId, messages);
            }
            executor.submit((Runnable) () -> {
                List<Message> messageList = SQLite.select().from(Message.class).where(Message_Table.bufferInfo_id.eq(bufferId)).queryList();
                messages.addAll(messageList);
            });
            filteredBacklogs.put(bufferId, filteredMessages);
            synchronized (filterSet) {
                filters.put(bufferId, backlogFilter);
                filterSet.add(backlogFilter);
            }
        }
    }
}
