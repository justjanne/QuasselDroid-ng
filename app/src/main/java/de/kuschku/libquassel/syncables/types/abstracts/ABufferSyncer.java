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

import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferSyncer;

public abstract class ABufferSyncer<T extends ABufferSyncer<T>> extends SyncableObject<T> implements QBufferSyncer {
    @Override
    public void requestSetLastSeenMsg(int buffer, int msgId) {
        _requestSetLastSeenMsg(buffer, msgId);
        syncVar("requestSetLastSeenMsg", buffer, msgId);
    }

    @Override
    public void requestSetMarkerLine(int buffer, int msgId) {
        _requestSetMarkerLine(buffer, msgId);
        syncVar("requestSetMarkerLine", buffer, msgId);
    }

    @Override
    public void requestRemoveBuffer(int buffer) {
        _requestRemoveBuffer(buffer);
        syncVar("requestRemoveBuffer", buffer);
    }

    @Override
    public void removeBuffer(int buffer) {
        _removeBuffer(buffer);
        requestRemoveBuffer(buffer);
    }

    @Override
    public void requestRenameBuffer(int buffer, String newName) {
        _requestRenameBuffer(buffer, newName);
        syncVar("requestRenameBuffer", buffer, newName);
    }

    @Override
    public void renameBuffer(int buffer, String newName) {
        _renameBuffer(buffer, newName);
        requestRenameBuffer(buffer, newName);
    }

    @Override
    public void requestMergeBuffersPermanently(int buffer1, int buffer2) {
        _requestMergeBuffersPermanently(buffer1, buffer2);
        syncVar("requestMergeBuffersPermanently", buffer1, buffer2);
    }

    @Override
    public void mergeBuffersPermanently(int buffer1, int buffer2) {
        _mergeBuffersPermanently(buffer1, buffer2);
        requestMergeBuffersPermanently(buffer1, buffer2);
    }

    @Override
    public void requestPurgeBufferIds() {
        _requestPurgeBufferIds();
        syncVar("requestPurgeBufferIds");
    }

    @Override
    public void requestMarkBufferAsRead(int buffer) {
        _requestMarkBufferAsRead(buffer);
        syncVar("requestMarkBufferAsRead", buffer);
    }

    @Override
    public void markBufferAsRead(int buffer) {
        _markBufferAsRead(buffer);
        syncVar("markBufferAsRead", buffer);
    }

    @Override
    public void setMarkerLine(int buffer, int msgId) {
        _setMarkerLine(buffer, msgId);
        syncVar("setMarkerLine", buffer, msgId);
    }

    @Override
    public void setLastSeenMsg(int buffer, int msgId) {
        _setLastSeenMsg(buffer, msgId);
        syncVar("setLastSeenMsg", buffer, msgId);
    }
}
