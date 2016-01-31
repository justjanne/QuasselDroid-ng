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

package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.BufferViewConfigSerializer;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.lists.IObservableList;
import de.kuschku.util.observables.lists.ObservableElementList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class BufferViewConfig extends SyncableObject<BufferViewConfig> {
    private String bufferViewName;
    private List<Integer> temporarilyRemovedBuffers;
    private boolean hideInactiveNetworks;
    private IObservableList<ElementCallback<Integer>, Integer> buffers;
    @NonNull
    private final IObservableList<ElementCallback<Integer>, Integer> NetworkList = new ObservableElementList<>();
    private int allowedBufferTypes;
    private boolean sortAlphabetically;
    private boolean disableDecoration;
    private boolean addNewBuffersAutomatically;
    private int networkId;
    private int minimumActivity;
    private boolean hideInactiveBuffers;
    private List<Integer> removedBuffers;
    private Client client;

    public BufferViewConfig(String bufferViewName, List<Integer> temporarilyRemovedBuffers, boolean hideInactiveNetworks, @NonNull List<Integer> bufferList, int allowedBufferTypes, boolean sortAlphabetically, boolean disableDecoration, boolean addNewBuffersAutomatically, int networkId, int minimumActivity, boolean hideInactiveBuffers, List<Integer> removedBuffers) {
        this.bufferViewName = bufferViewName;
        this.temporarilyRemovedBuffers = temporarilyRemovedBuffers;
        this.hideInactiveNetworks = hideInactiveNetworks;
        this.buffers = new ObservableElementList<>(bufferList);
        this.allowedBufferTypes = allowedBufferTypes;
        this.sortAlphabetically = sortAlphabetically;
        this.disableDecoration = disableDecoration;
        this.addNewBuffersAutomatically = addNewBuffersAutomatically;
        this.networkId = networkId;
        this.minimumActivity = minimumActivity;
        this.hideInactiveBuffers = hideInactiveBuffers;
        this.removedBuffers = removedBuffers;
    }

    public String getBufferViewName() {
        return bufferViewName;
    }

    public void setBufferViewName(String bufferViewName) {
        this.bufferViewName = bufferViewName;
    }

    public List<Integer> getTemporarilyRemovedBuffers() {
        return temporarilyRemovedBuffers;
    }

    public void setTemporarilyRemovedBuffers(List<Integer> temporarilyRemovedBuffers) {
        this.temporarilyRemovedBuffers = temporarilyRemovedBuffers;
    }

    public boolean isHideInactiveNetworks() {
        return hideInactiveNetworks;
    }

    public void setHideInactiveNetworks(boolean hideInactiveNetworks) {
        this.hideInactiveNetworks = hideInactiveNetworks;
    }


    public void SYNC_setHideInactiveNetworks(boolean hideInactiveNetworks) {
        if (this.hideInactiveNetworks == hideInactiveNetworks) return;
        setHideInactiveNetworks(hideInactiveBuffers);
        sync("setHideInactiveBuffers", new Object[]{hideInactiveNetworks});
    }

    public IObservableList<ElementCallback<Integer>, Integer> getBuffers() {
        return buffers;
    }

    public void setBuffers(IObservableList<ElementCallback<Integer>, Integer> buffers) {
        this.buffers = buffers;
    }

    public void setBufferList(@NonNull List<Integer> bufferList) {
        buffers = new ObservableElementList<>(bufferList);
    }

    public int getAllowedBufferTypes() {
        return allowedBufferTypes;
    }

    public void setAllowedBufferTypes(int allowedBufferTypes) {
        this.allowedBufferTypes = allowedBufferTypes;
    }

    public boolean isSortAlphabetically() {
        return sortAlphabetically;
    }

    public void setSortAlphabetically(boolean sortAlphabetically) {
        this.sortAlphabetically = sortAlphabetically;
    }

    public boolean isDisableDecoration() {
        return disableDecoration;
    }

    public void setDisableDecoration(boolean disableDecoration) {
        this.disableDecoration = disableDecoration;
    }

    public boolean isAddNewBuffersAutomatically() {
        return addNewBuffersAutomatically;
    }

    public void setAddNewBuffersAutomatically(boolean addNewBuffersAutomatically) {
        this.addNewBuffersAutomatically = addNewBuffersAutomatically;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
        if (this.networkId != 0) {
            if (client.getNetworks().contains(networkId))
                this.NetworkList.add(networkId);
        } else {
            this.NetworkList.addAll(client.getNetworks());
        }
    }

    public int getMinimumActivity() {
        return minimumActivity;
    }

    public void setMinimumActivity(int minimumActivity) {
        this.minimumActivity = minimumActivity;
    }

    public boolean isHideInactiveBuffers() {
        return hideInactiveBuffers;
    }

    public void setHideInactiveBuffers(boolean hideInactiveBuffers) {
        this.hideInactiveBuffers = hideInactiveBuffers;
    }

    public void SYNC_setHideInactiveBuffers(boolean hideInactiveBuffers) {
        if (this.hideInactiveBuffers == hideInactiveBuffers) return;
        setHideInactiveBuffers(hideInactiveBuffers);
        sync("setHideInactiveBuffers", new Object[]{hideInactiveBuffers});
    }

    public List<Integer> getRemovedBuffers() {
        return removedBuffers;
    }

    public void setRemovedBuffers(List<Integer> removedBuffers) {
        this.removedBuffers = removedBuffers;
    }

    @NonNull
    @Override
    public String toString() {
        return "BufferViewConfig{" +
                "bufferViewName='" + bufferViewName + '\'' +
                ", temporarilyRemovedBuffers=" + temporarilyRemovedBuffers +
                ", hideInactiveNetworks=" + hideInactiveNetworks +
                ", buffers=" + buffers +
                ", allowedBufferTypes=" + allowedBufferTypes +
                ", sortAlphabetically=" + sortAlphabetically +
                ", disableDecoration=" + disableDecoration +
                ", addNewBuffersAutomatically=" + addNewBuffersAutomatically +
                ", networkId=" + networkId +
                ", minimumActivity=" + minimumActivity +
                ", hideInactiveBuffers=" + hideInactiveBuffers +
                ", removedBuffers=" + removedBuffers +
                '}';
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {
        this.client = client;
        setObjectName(function.objectName);
        assertNotNull(client.getBufferViewManager());
        client.getBufferViewManager().BufferViews.put(Integer.valueOf(function.objectName), this);
    }

    @Override
    public void update(@NonNull BufferViewConfig from) {
        this.bufferViewName = from.bufferViewName;
        this.temporarilyRemovedBuffers = from.temporarilyRemovedBuffers;
        this.hideInactiveNetworks = from.hideInactiveNetworks;
        this.buffers = from.buffers;
        this.allowedBufferTypes = from.allowedBufferTypes;
        this.sortAlphabetically = from.sortAlphabetically;
        this.disableDecoration = from.disableDecoration;
        this.addNewBuffersAutomatically = from.addNewBuffersAutomatically;
        this.networkId = from.networkId;
        this.minimumActivity = from.minimumActivity;
        this.hideInactiveBuffers = from.hideInactiveBuffers;
        this.removedBuffers = from.removedBuffers;
    }

    @Override
    public void update(@NonNull Map<String, QVariant> from) {
        update(BufferViewConfigSerializer.get().fromDatastream(from));
    }

    public void addBuffer(int bufferId, int pos) {
        if (buffers.contains(bufferId))
            return;

        if (pos < 0)
            pos = 0;
        if (pos > buffers.size())
            pos = buffers.size();

        if (removedBuffers.contains(bufferId))
            removedBuffers.remove(removedBuffers.indexOf(bufferId));

        if (temporarilyRemovedBuffers.contains(bufferId))
            temporarilyRemovedBuffers.remove(temporarilyRemovedBuffers.indexOf(bufferId));

        buffers.add(pos, bufferId);
    }

    public void SYNC_addBuffer(int bufferId, int position) {
        addBuffer(bufferId, position);
        sync("addBuffer", new Object[]{bufferId, position});
    }

    public void moveBuffer(int bufferId, int pos) {
        if (!buffers.contains(bufferId))
            return;

        if (pos < 0)
            pos = 0;
        if (pos >= buffers.size())
            pos = buffers.size() - 1;

        int index = buffers.indexOf(bufferId);
        if (pos == index)
            return;
        if (pos > index)
            pos--;

        buffers.remove(index);
        buffers.add(pos, bufferId);
    }

    public void SYNC_moveBuffer(int bufferId, int position) {
        moveBuffer(bufferId, position);
        sync("moveBuffer", new Object[]{bufferId, position});
    }

    public void removeBuffer(int bufferId) {
        if (buffers.contains(bufferId))
            buffers.remove(buffers.indexOf(bufferId));
        if (removedBuffers.contains(bufferId))
            removedBuffers.remove(removedBuffers.indexOf(bufferId));
        temporarilyRemovedBuffers.add(bufferId);
    }

    public void SYNC_removeBuffer(int bufferId) {
        removeBuffer(bufferId);
        sync("removeBuffer", new Object[]{bufferId});
    }

    public void removeBufferPermanently(int bufferId) {
        if (buffers.contains(bufferId))
            buffers.remove(buffers.indexOf(bufferId));
        if (temporarilyRemovedBuffers.contains(bufferId))
            temporarilyRemovedBuffers.remove(temporarilyRemovedBuffers.indexOf(bufferId));
        removedBuffers.add(bufferId);
    }

    public void SYNC_removeBufferPermanently(int bufferId) {
        removeBufferPermanently(bufferId);
        sync("removeBufferPermanently", new Object[]{bufferId});
    }


    @NonNull
    public IObservableList<ElementCallback<Integer>, Integer> getNetworkList() {
        return NetworkList;
    }

    public void doLateInit() {
        NetworkList.clear();
        // This should initialize the network list
        setNetworkId(getNetworkId());
    }
}
