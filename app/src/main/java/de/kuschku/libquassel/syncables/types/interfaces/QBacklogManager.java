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

package de.kuschku.libquassel.syncables.types.interfaces;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Set;

import de.kuschku.libquassel.localtypes.BacklogFilter;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.Synced;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;

public interface QBacklogManager<T extends QSyncableObject<T>> extends QSyncableObject<T> {
    void requestMoreBacklog(int bufferId, int amount);

    void requestBacklogInitial(int id, int amount);

    @Synced
    void requestBacklog(int id, int first, int last, int limit, int additional);

    void _requestBacklog(int id, int first, int last, int limit, int additional);

    @Synced
    void receiveBacklog(int id, int first, int last, int limit, int additional, List<Message> messages);

    void _receiveBacklog(int id, int first, int last, int limit, int additional, List<Message> messages);

    @Synced
    void requestBacklogAll(int first, int last, int limit, int additional);

    void _requestBacklogAll(int first, int last, int limit, int additional);

    @Synced
    void receiveBacklogAll(int first, int last, int limit, int additional, List<Message> messages);

    void _receiveBacklogAll(int first, int last, int limit, int additional, List<Message> messages);

    @NonNull
    BacklogFilter filter(int id);

    @NonNull
    ObservableComparableSortedList<Message> unfiltered(int id);

    @NonNull
    ObservableComparableSortedList<Message> filtered(int id);

    void open(int bufferId);
    int open();

    void receiveBacklog(Message msg);

    int waitingMax();

    @NonNull
    Set<Integer> waiting();
}
