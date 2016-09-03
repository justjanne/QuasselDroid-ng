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
import java.util.Map;

import de.kuschku.libquassel.syncables.Synced;
import de.kuschku.util.observables.lists.ObservableSortedList;

public interface QBufferViewManager extends QSyncableObject<QBufferViewManager> {
    @NonNull
    ObservableSortedList<QBufferViewConfig> bufferViewConfigs();

    QBufferViewConfig bufferViewConfig(int bufferViewId);

    void _addBufferViewConfig(int bufferViewConfigId);

    void _addBufferViewConfig(final QBufferViewConfig bufferViewConfig);

    @Synced
    void createBufferView(final QBufferViewConfig bufferView);

    @Synced
    void createBufferViews(final List<QBufferViewConfig> bufferViewConfigs);

    @Synced
    void deleteBufferView(int bufferViewId);

    @Synced
    void deleteBufferViews(final List<Integer> bufferViews);

    void _deleteBufferViewConfig(int bufferViewConfigId);

    void checkForNewBuffers(int bufferId);

    Map<Integer, QBufferViewConfig> bufferViewConfigMap();
}
