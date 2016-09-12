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

import java.util.List;
import java.util.Set;

import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.localtypes.BacklogFilter;
import de.kuschku.libquassel.message.Message;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;

public interface BacklogStorage {
    @NonNull
    ObservableComparableSortedList<Message> getUnfiltered(@IntRange(from = 0) int bufferid);

    @NonNull
    ObservableComparableSortedList<Message> getFiltered(@IntRange(from = 0) int bufferid);

    @NonNull
    BacklogFilter getFilter(@IntRange(from = 0) int bufferid);

    int getLatest(@IntRange(from = 0) int bufferid);

    void insertMessages(@IntRange(from = 0) int bufferId, Message... messages);

    void insertMessages(@IntRange(from = 0) int bufferId, List<Message> messages);

    void insertMessages(Message... messages);

    void insertMessages(List<Message> messages);

    void setClient(Client client);

    void markBufferUnused(@IntRange(from = 0) int bufferid);

    void clear(@IntRange(from = 0) int bufferid);

    Set<BacklogFilter> getFilters();

    void setMarkerLine(@IntRange(from = 0) int buffer, int msgId);

    void merge(@IntRange(from = 0) int buffer1, @IntRange(from = 0) int buffer2);
}
