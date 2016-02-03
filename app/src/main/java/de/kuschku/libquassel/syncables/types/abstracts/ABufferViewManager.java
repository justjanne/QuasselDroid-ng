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

import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewManager;

public abstract class ABufferViewManager<T extends ABufferViewManager<T>> extends SyncableObject<T> implements QBufferViewManager {
    @Override
    public void addBufferViewConfig(QBufferViewConfig config) {
        _addBufferViewConfig(config);
        requestCreateBufferView(config);

    }

    @Override
    public void addBufferViewConfig(int bufferViewConfigId) {
        _addBufferViewConfig(bufferViewConfigId);
        syncVar("addBufferViewConfig", bufferViewConfigId);

    }

    @Override
    public void newBufferViewConfig(int bufferViewConfigId) {
        _newBufferViewConfig(bufferViewConfigId);
        syncVar("newBufferViewConfig", bufferViewConfigId);

    }

    @Override
    public void deleteBufferViewConfig(int bufferViewConfigId) {
        _deleteBufferViewConfig(bufferViewConfigId);
        requestDeleteBufferView(bufferViewConfigId);

    }

    @Override
    public void requestCreateBufferView(QBufferViewConfig bufferView) {
        _requestCreateBufferView(bufferView);
        syncVar("requestCreateBufferView", bufferView);

    }

    @Override
    public void requestDeleteBufferView(int bufferViewId) {
        _requestDeleteBufferView(bufferViewId);
        syncVar("requestDeleteBufferView", bufferViewId);

    }

    @Override
    public void requestDeleteBufferViews(List<Integer> bufferViews) {
        _requestDeleteBufferViews(bufferViews);
        syncVar("requestDeleteBufferViews", bufferViews);

    }
}
