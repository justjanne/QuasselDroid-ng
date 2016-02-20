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

import android.databinding.ObservableInt;

import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.Synced;

public interface QBufferSyncer extends QObservable {
    int lastSeenMsg(int buffer);

    int markerLine(int buffer);

    @Synced
    void setLastSeenMsg(int buffer, final int msgId);

    void _setLastSeenMsg(int buffer, final int msgId);

    @Synced
    void requestSetLastSeenMsg(int buffer, final int msgId);

    void _requestSetLastSeenMsg(int buffer, final int msgId);

    @Synced
    void setMarkerLine(int buffer, final int msgId);

    void _setMarkerLine(int buffer, final int msgId);

    @Synced
    void requestSetMarkerLine(int buffer, final int msgId);

    void _requestSetMarkerLine(int buffer, final int msgId);

    @Synced
    void requestRemoveBuffer(int buffer);

    void _requestRemoveBuffer(int buffer);

    @Synced
    void removeBuffer(int buffer);

    void _removeBuffer(int buffer);

    @Synced
    void requestRenameBuffer(int buffer, String newName);

    void _requestRenameBuffer(int buffer, String newName);

    @Synced
    void renameBuffer(int buffer, String newName);

    void _renameBuffer(int buffer, String newName);

    @Synced
    void requestMergeBuffersPermanently(int buffer1, int buffer2);

    void _requestMergeBuffersPermanently(int buffer1, int buffer2);

    @Synced
    void mergeBuffersPermanently(int buffer1, int buffer2);

    void _mergeBuffersPermanently(int buffer1, int buffer2);

    @Synced
    void requestPurgeBufferIds();

    void _requestPurgeBufferIds();

    @Synced
    void requestMarkBufferAsRead(int buffer);

    void _requestMarkBufferAsRead(int buffer);

    @Synced
    void markBufferAsRead(int buffer);

    void _markBufferAsRead(int buffer);


    ObservableInt activity(int bufferid);

    void setActivity(int bufferid, int activity);

    void addActivity(int bufferid, int activity);

    void addActivity(int bufferid, Message.Type type);

    void addActivity(Message message);
}
