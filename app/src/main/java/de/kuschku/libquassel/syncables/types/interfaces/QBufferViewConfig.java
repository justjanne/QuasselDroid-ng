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

import de.kuschku.libquassel.syncables.Synced;
import de.kuschku.util.observables.lists.ObservableList;
import de.kuschku.util.observables.lists.ObservableSet;

public interface QBufferViewConfig extends QObservable {

    int bufferViewId();

    String bufferViewName();

    @Synced
    void setBufferViewName(final String bufferViewName);

    void _setBufferViewName(final String bufferViewName);

    int networkId();

    @Synced
    void setNetworkId(final int networkId);

    void _setNetworkId(final int networkId);

    boolean addNewBuffersAutomatically();

    @Synced
    void setAddNewBuffersAutomatically(boolean addNewBuffersAutomatically);

    void _setAddNewBuffersAutomatically(boolean addNewBuffersAutomatically);

    boolean sortAlphabetically();

    @Synced
    void setSortAlphabetically(boolean sortAlphabetically);

    void _setSortAlphabetically(boolean sortAlphabetically);

    boolean disableDecoration();

    @Synced
    void setDisableDecoration(boolean disableDecoration);

    void _setDisableDecoration(boolean disableDecoration);

    int allowedBufferTypes();

    @Synced
    void setAllowedBufferTypes(int bufferTypes);

    void _setAllowedBufferTypes(int bufferTypes);

    int minimumActivity();

    @Synced
    void setMinimumActivity(int activity);

    void _setMinimumActivity(int activity);

    boolean hideInactiveBuffers();

    @Synced
    void setHideInactiveBuffers(boolean hideInactiveBuffers);

    void _setHideInactiveBuffers(boolean hideInactiveBuffers);

    boolean hideInactiveNetworks();

    @Synced
    void setHideInactiveNetworks(boolean hideInactiveNetworks);

    void _setHideInactiveNetworks(boolean hideInactiveNetworks);

    @Synced
    void requestSetBufferViewName(final String bufferViewName);

    void _requestSetBufferViewName(final String bufferViewName);

    @NonNull
    ObservableList<Integer> bufferList();

    @NonNull
    ObservableSet<Integer> bufferIds();

    @NonNull
    ObservableSet<Integer> removedBuffers();

    @NonNull
    ObservableSet<Integer> temporarilyRemovedBuffers();

    @Synced
    void addBuffer(final int bufferId, int pos);

    void _addBuffer(final int bufferId, int pos);

    @Synced
    void requestAddBuffer(final int bufferId, int pos);

    void _requestAddBuffer(final int bufferId, int pos);

    @Synced
    void moveBuffer(final int bufferId, int pos);

    void _moveBuffer(final int bufferId, int pos);

    @Synced
    void requestMoveBuffer(final int bufferId, int pos);

    void _requestMoveBuffer(final int bufferId, int pos);

    @Synced
    void removeBuffer(final int bufferId);

    void _removeBuffer(final int bufferId);

    @Synced
    void requestRemoveBuffer(final int bufferId);

    void _requestRemoveBuffer(final int bufferId);

    @Synced
    void removeBufferPermanently(final int bufferId);

    void _removeBufferPermanently(final int bufferId);

    @Synced
    void requestRemoveBufferPermanently(final int bufferId);

    void _requestRemoveBufferPermanently(final int bufferId);

    void init(int bufferViewConfigId);

    ObservableSet<QNetwork> networkList();

    void updateNetworks();
}
