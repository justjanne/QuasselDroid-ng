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

package de.kuschku.libquassel.syncables.types.abstracts;

import java.util.List;

import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.interfaces.QBacklogManager;

public abstract class ABacklogManager<T extends ABacklogManager<T>> extends SyncableObject<T> implements QBacklogManager<T> {
    static final String intName = QMetaType.Type.Int.getSerializableName();

    @Override
    public void requestBacklog(int id, int first, int last, int limit, int additional) {
        _requestBacklog(id, first, last, limit, additional);
        sync("requestBacklog", new String[]{"BufferId", "MsgId", "MsgId", intName, intName}, new Object[]{id, first, last, limit, additional});
    }

    @Override
    public void receiveBacklog(int id, int first, int last, int limit, int additional, List<Message> messages) {
        _receiveBacklog(id, first, last, limit, additional, messages);
        sync("receiveBacklog", new String[]{"BufferId", "MsgId", "MsgId", intName, intName}, new Object[]{id, first, last, limit, additional, messages});
    }

    @Override
    public void requestBacklogAll(int first, int last, int limit, int additional) {
        _requestBacklogAll(first, last, limit, additional);
        sync("requestBacklogAll", new String[]{"MsgId", "MsgId", intName, intName}, new Object[]{first, last, limit, additional});
    }

    @Override
    public void receiveBacklogAll(int first, int last, int limit, int additional, List<Message> messages) {
        _receiveBacklogAll(first, last, limit, additional, messages);
        sync("receiveBacklogAll", new String[]{"MsgId", "MsgId", intName, intName}, new Object[]{first, last, limit, additional, messages});
    }
}
