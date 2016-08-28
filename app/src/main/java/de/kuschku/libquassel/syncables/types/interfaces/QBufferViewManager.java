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

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.Synced;

public interface QBufferViewManager extends QObservable {
    @NonNull
    List<QBufferViewConfig> bufferViewConfigs();

    QBufferViewConfig bufferViewConfig(int bufferViewId);

    @Synced
    void addBufferViewConfig(QBufferViewConfig config);

    void _addBufferViewConfig(QBufferViewConfig config);

    @Synced
    void addBufferViewConfig(int bufferViewConfigId);

    void _addBufferViewConfig(int bufferViewConfigId);

    @Synced
    void newBufferViewConfig(int bufferViewConfigId);

    void _newBufferViewConfig(int bufferViewConfigId);

    @Synced
    void deleteBufferViewConfig(int bufferViewConfigId);

    void _deleteBufferViewConfig(int bufferViewConfigId);

    //QVariant(QVariantMap, QMap(("BufferList", QVariant(QVariantList, () ) ) ( "RemovedBuffers" ,  QVariant(QVariantList, () ) ) ( "TemporarilyRemovedBuffers" ,  QVariant(QVariantList, () ) ) ( "addNewBuffersAutomatically" ,  QVariant(bool, true) ) ( "allowedBufferTypes" ,  QVariant(int, 15) ) ( "bufferViewName" ,  QVariant(QString, "All Chats") ) ( "disableDecoration" ,  QVariant(bool, false) ) ( "hideInactiveBuffers" ,  QVariant(bool, false) ) ( "hideInactiveNetworks" ,  QVariant(bool, false) ) ( "minimumActivity" ,  QVariant(int, 0) ) ( "networkId" ,  QVariant(NetworkId, ) ) ( "sortAlphabetically" ,  QVariant(bool, true) ) )
    @Synced
    void requestCreateBufferView(final QBufferViewConfig bufferView);

    void _requestCreateBufferView(final QBufferViewConfig bufferView);

    //@Synced void requestCreateBufferView(final Map<String, QVariant> properties);
    //@Synced void requestCreateBufferViews(final List<QVariant> properties);

    @Synced
    void requestDeleteBufferView(int bufferViewId);

    void _requestDeleteBufferView(int bufferViewId);

    @Synced
    void requestDeleteBufferViews(final List<Integer> bufferViews);

    void _requestDeleteBufferViews(final List<Integer> bufferViews);

    void checkForNewBuffers(int bufferId);
}
