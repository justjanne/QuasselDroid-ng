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

import java.util.List;

import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.util.observables.lists.ObservableSortedList;

public abstract class BacklogManager<T extends BacklogManager<T>> extends SyncableObject<T> {
    public abstract void requestBacklog(int bufferId, int from, int to, int count, int extra);

    public abstract void receiveBacklog(int bufferId, int from, int to, int count, int extra, @NonNull List<Message> messages);

    public abstract void displayMessage(int bufferId, @NonNull Message message);

    public abstract ObservableSortedList<Message> get(@IntRange(from = -1) int bufferId);

    public abstract ObservableSortedList<Message> getFiltered(@IntRange(from = -1) int bufferId);

    public abstract BacklogFilter getFilter(@IntRange(from = -1) int bufferId);

    public abstract void requestMoreBacklog(int bufferId, int count);

    public abstract void setClient(Client client);
}
