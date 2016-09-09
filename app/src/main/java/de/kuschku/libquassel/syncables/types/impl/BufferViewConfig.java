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

package de.kuschku.libquassel.syncables.types.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.BufferViewConfigSerializer;
import de.kuschku.libquassel.syncables.types.abstracts.ABufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.util.observables.lists.ObservableList;
import de.kuschku.util.observables.lists.ObservableSet;

import static de.kuschku.libquassel.primitives.types.BufferInfo.Type;

public class BufferViewConfig extends ABufferViewConfig {
    @NonNull
    private final ObservableList<Integer> buffers;
    @NonNull
    private final ObservableSet<Integer> visibleBufferIds;
    @NonNull
    private final ObservableSet<Integer> allBufferIds;
    @NonNull
    private final ObservableSet<Integer> removedBuffers;
    @NonNull
    private final ObservableSet<Integer> temporarilyRemovedBuffers;
    private final ObservableSet<QNetwork> networkList = new ObservableSet<>();
    private int bufferViewId;
    private String bufferViewName;
    private int networkId;
    private boolean addNewBuffersAutomatically;
    private boolean sortAlphabetically;
    private boolean disableDecoration;
    private int allowedBufferTypes;
    private MinimumActivity minimumActivity;
    private boolean hideInactiveBuffers;
    private boolean hideInactiveNetworks;

    public BufferViewConfig(String bufferViewName, @NonNull List<Integer> temporarilyRemovedBuffers, boolean hideInactiveNetworks, @NonNull List<Integer> buffers, int allowedBufferTypes, boolean sortAlphabetically, boolean disableDecoration, boolean addNewBuffersAutomatically, int networkId, MinimumActivity minimumActivity, boolean hideInactiveBuffers, @NonNull List<Integer> removedBuffers) {
        this.bufferViewName = bufferViewName;
        this.temporarilyRemovedBuffers = new ObservableSet<>(temporarilyRemovedBuffers);
        this.hideInactiveNetworks = hideInactiveNetworks;
        this.buffers = new ObservableList<>(buffers);
        buffers.removeAll(removedBuffers);
        buffers.removeAll(temporarilyRemovedBuffers);
        this.visibleBufferIds = new ObservableSet<>();
        visibleBufferIds.addAll(buffers);
        this.allBufferIds = new ObservableSet<>();
        allBufferIds.addAll(buffers);
        allBufferIds.addAll(removedBuffers);
        allBufferIds.addAll(temporarilyRemovedBuffers);
        this.allowedBufferTypes = allowedBufferTypes;
        this.sortAlphabetically = sortAlphabetically;
        this.disableDecoration = disableDecoration;
        this.addNewBuffersAutomatically = addNewBuffersAutomatically;
        this.networkId = networkId;
        this.minimumActivity = minimumActivity;
        this.hideInactiveBuffers = hideInactiveBuffers;
        this.removedBuffers = new ObservableSet<>(removedBuffers);
    }

    @NonNull
    public static BufferViewConfig create(int bufferViewConfigId) {
        BufferViewConfig bufferViewConfig = new BufferViewConfig(
                "",
                Collections.emptyList(),
                false,
                Collections.emptyList(),
                Type.STATUS.id | Type.QUERY.id | Type.CHANNEL.id | Type.GROUP.id,
                true,
                false,
                true,
                -1,
                MinimumActivity.NONE,
                false,
                Collections.emptyList()
        );
        bufferViewConfig.init(bufferViewConfigId);
        return bufferViewConfig;
    }

    @Override
    public int bufferViewId() {
        return bufferViewId;
    }

    @Override
    public String bufferViewName() {
        return bufferViewName;
    }

    @Override
    public void _setBufferViewName(String bufferViewName) {
        this.bufferViewName = bufferViewName;
        _update();
    }

    @Override
    public int networkId() {
        return networkId;
    }

    @Override
    public void _setNetworkId(int networkId) {
        this.networkId = networkId;
        if (initialized)
            updateNetworks();
        _update();
    }

    @Override
    public void updateNetworks() {
        if (this.networkId == 0 || this.networkId == -1) {
            networkList.retainAll(client.networkManager().networks());
            networkList.addAll(client.networkManager().networks());
        } else {
            networkList.retainAll(Collections.singleton(client.networkManager().network(this.networkId)));
            networkList.add(client.networkManager().network(this.networkId));
        }
    }

    @Override
    public void checkAddBuffer(int id) {
        if (!allBufferIds.contains(id)) {
            Buffer buffer1 = client.bufferManager().buffer(id);
            if (addNewBuffersAutomatically && buffer1 != null) {
                BufferInfo info = buffer1.getInfo();
                int pos;
                if (!sortAlphabetically) {
                    pos = visibleBufferIds.size();
                } else {
                    List<String> infos = new ArrayList<>();
                    for (int bufferId : bufferList()) {
                        Buffer buffer = client.bufferManager().buffer(bufferId);
                        if (buffer == null)
                            Log.w("libquassel", "Buffer is null: " + bufferId + " while adding buffer " + info);
                        BufferInfo info1 = buffer.getInfo();
                        if (info1.networkId == info.networkId)
                            infos.add(info1.name);
                    }
                    infos.add(info.name);
                    Collections.sort(infos);
                    pos = infos.indexOf(info.name);
                }
                requestAddBuffer(info.id, pos);
            } else {
                requestRemoveBufferPermanently(id);
            }
        }
    }

    @Override
    public DisplayType mayDisplay(Buffer buffer) {
        if (buffer != null &&
                (allowedBufferTypes == 0 || (0 != (buffer.getInfo().type.id & allowedBufferTypes()))) &&
                (networkId == 0 || (networkId == buffer.getInfo().networkId))
                ) {
            int bufferid = buffer.getInfo().id;
            if (visibleBufferIds.contains(bufferid) && !temporarilyRemovedBuffers.contains(bufferid) && !removedBuffers.contains(bufferid))
                return DisplayType.ALWAYS;
            else if (temporarilyRemovedBuffers.contains(bufferid) && !removedBuffers.contains(bufferid))
                return DisplayType.TEMP_HIDDEN;
            else
                return DisplayType.PERM_HIDDEN;
        } else {
            return DisplayType.NONE;
        }
    }

    @Override
    public boolean addNewBuffersAutomatically() {
        return addNewBuffersAutomatically;
    }

    @Override
    public void _setAddNewBuffersAutomatically(boolean addNewBuffersAutomatically) {
        this.addNewBuffersAutomatically = addNewBuffersAutomatically;
        _update();
    }

    @Override
    public boolean sortAlphabetically() {
        return sortAlphabetically;
    }

    @Override
    public void _setSortAlphabetically(boolean sortAlphabetically) {
        this.sortAlphabetically = sortAlphabetically;
        _update();
    }

    @Override
    public boolean disableDecoration() {
        return disableDecoration;
    }

    @Override
    public void _setDisableDecoration(boolean disableDecoration) {
        this.disableDecoration = disableDecoration;
        _update();
    }

    @Override
    public int allowedBufferTypes() {
        return allowedBufferTypes;
    }

    @Override
    public boolean isBufferTypeAllowed(Type type) {
        return (allowedBufferTypes & type.id) != 0;
    }

    @Override
    public void setBufferTypeAllowed(Type type, boolean allowed) {
        int masked = allowedBufferTypes & ~type.id;
        _setAllowedBufferTypes(masked | (allowed ? type.id : 0));
    }

    @Override
    public void _setAllowedBufferTypes(int bufferTypes) {
        this.allowedBufferTypes = bufferTypes;
        _update();
    }

    @Override
    public MinimumActivity minimumActivity() {
        return minimumActivity;
    }

    @Override
    public void _setMinimumActivity(MinimumActivity activity) {
        this.minimumActivity = activity;
        _update();
    }

    @Override
    public void _setMinimumActivity(int activity) {
        _setMinimumActivity(MinimumActivity.fromId(activity));
    }

    @Override
    public boolean hideInactiveBuffers() {
        return hideInactiveBuffers;
    }

    @Override
    public void _setHideInactiveBuffers(boolean hideInactiveBuffers) {
        this.hideInactiveBuffers = hideInactiveBuffers;
        _update();
    }

    @Override
    public boolean hideInactiveNetworks() {
        return hideInactiveNetworks;
    }

    @Override
    public void _setHideInactiveNetworks(boolean hideInactiveNetworks) {
        this.hideInactiveNetworks = hideInactiveNetworks;
        _update();
    }

    @Override
    public void _requestSetBufferViewName(String bufferViewName) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @NonNull
    @Override
    public ObservableList<Integer> bufferList() {
        return buffers;
    }

    @NonNull
    @Override
    public ObservableSet<Integer> bufferIds() {
        return visibleBufferIds;
    }

    @NonNull
    @Override
    public ObservableSet<Integer> allBufferIds() {
        return allBufferIds;
    }

    @Override
    public void checkAddBuffers() {
        for (int bufferId : client.bufferManager().bufferIds()) {
            checkAddBuffer(bufferId);
        }
    }

    @NonNull
    @Override
    public ObservableSet<Integer> removedBuffers() {
        return removedBuffers;
    }

    @NonNull
    @Override
    public ObservableSet<Integer> temporarilyRemovedBuffers() {
        return temporarilyRemovedBuffers;
    }

    @Override
    public void _addBuffer(int bufferId, int pos) {
        if (client.bufferManager().buffer(bufferId) == null) {
            client.bufferBuffer(this, bufferId, pos);
            return;
        }

        if (buffers.contains(bufferId))
            return;

        if (pos < 0)
            pos = 0;
        else if (pos > buffers.size())
            pos = buffers.size();

        if (removedBuffers.contains(bufferId))
            removedBuffers.remove(bufferId);

        if (temporarilyRemovedBuffers.contains(bufferId))
            temporarilyRemovedBuffers.remove(bufferId);

        buffers.add(pos, bufferId);
        visibleBufferIds.add(bufferId);
        allBufferIds.add(bufferId);
    }

    @Override
    public void _requestAddBuffer(int bufferId, int pos) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _moveBuffer(int bufferId, int pos) {
        if (!buffers.contains(bufferId))
            return;

        if (pos < 0)
            pos = 0;
        else if (pos >= buffers.size())
            pos = buffers.size() - 1;

        // If we want to add the buffer after the position it was before, we have to decrement the
        // position by one.
        // Example: Before [a, b, c, d, e]
        //          Now we move c to the last spot (nullindex: 4)
        //          First we remove c from the current position:
        //          [a, b, d, e]
        //          Now, we want to add c at the end, right? But 4 is now out of bounds.
        //          So we decrement pos by one to make up for the loss of the element in the beginning
        if (pos > buffers.indexOf(bufferId))
            pos -= 1;

        buffers.remove(buffers.indexOf(bufferId));
        buffers.add(bufferId, pos);
        _update();
    }

    @Override
    public void _requestMoveBuffer(int bufferId, int pos) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _removeBuffer(int bufferId) {
        visibleBufferIds.remove(bufferId);
        if (buffers.contains(bufferId))
            buffers.remove((Integer) bufferId);

        if (removedBuffers.contains(bufferId))
            removedBuffers.remove(bufferId);

        temporarilyRemovedBuffers.add(bufferId);
        _update();
    }

    @Override
    public void _requestRemoveBuffer(int bufferId) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _removeBufferPermanently(int bufferId) {
        visibleBufferIds.remove(bufferId);
        if (buffers.contains(bufferId))
            buffers.remove((Integer) bufferId);

        if (temporarilyRemovedBuffers.contains(bufferId))
            temporarilyRemovedBuffers.remove(bufferId);

        removedBuffers.add(bufferId);
        _update();
    }

    @Override
    public void _requestRemoveBufferPermanently(int bufferId) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        bufferViewId = Integer.parseInt(objectName);
        super.init(objectName, provider, client);
        client.bufferViewManager()._addBufferViewConfig(this);
        updateNetworks();
        checkAddBuffers();
        _update();
    }

    @Override
    public void init(int bufferViewConfigId) {
        bufferViewId = bufferViewConfigId;
    }

    @Override
    public ObservableSet<QNetwork> networkList() {
        return networkList;
    }

    @Override
    public void deleteBuffer(int bufferId) {
        visibleBufferIds.remove(bufferId);
        allBufferIds.remove(bufferId);
        buffers.remove((Integer) bufferId);
        temporarilyRemovedBuffers.remove(bufferId);
        removedBuffers.remove(bufferId);
    }

    @Override
    public void _update(@NonNull Map<String, QVariant> from) {
        _update(BufferViewConfigSerializer.get().fromLegacy(from));
    }

    @Override
    public void _update(@NonNull QBufferViewConfig from) {
        this.bufferViewId = from.bufferViewId();
        this.bufferViewName = from.bufferViewName();
        this.networkId = from.networkId();
        this.addNewBuffersAutomatically = from.addNewBuffersAutomatically();
        this.sortAlphabetically = from.sortAlphabetically();
        this.disableDecoration = from.disableDecoration();
        this.allowedBufferTypes = from.allowedBufferTypes();
        this.minimumActivity = from.minimumActivity();
        this.hideInactiveBuffers = from.hideInactiveBuffers();
        this.hideInactiveNetworks = from.hideInactiveNetworks();
        this.buffers.clear();
        this.buffers.addAll(from.bufferList());
        this.visibleBufferIds.retainAll(from.bufferIds());
        this.visibleBufferIds.addAll(from.bufferIds());
        this.removedBuffers.retainAll(from.removedBuffers());
        this.removedBuffers.addAll(from.removedBuffers());
        this.temporarilyRemovedBuffers.retainAll(from.temporarilyRemovedBuffers());
        this.temporarilyRemovedBuffers.addAll(from.temporarilyRemovedBuffers());
        _update();
    }

    @Override
    public String toString() {
        return "BufferViewConfig{" +
                "bufferViewId=" + bufferViewId +
                ", bufferViewName='" + bufferViewName + '\'' +
                '}';
    }
}
