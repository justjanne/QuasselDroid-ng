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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.abstracts.ABufferViewManager;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewManager;
import de.kuschku.util.observables.callbacks.GeneralCallback;
import de.kuschku.util.observables.lists.AndroidObservableSortedList;

public class BufferViewManager extends ABufferViewManager {
    @NonNull
    final
    Set<Integer> cachedIds = new HashSet<>();
    final AndroidObservableSortedList<QBufferViewConfig> list = new AndroidObservableSortedList<>(QBufferViewConfig.class, new AndroidObservableSortedList.ItemComparator<QBufferViewConfig>() {
        @Override
        public int compare(QBufferViewConfig o1, QBufferViewConfig o2) {
            return o1.bufferViewName().compareToIgnoreCase(o2.bufferViewName());
        }

        @Override
        public boolean areContentsTheSame(QBufferViewConfig oldItem, QBufferViewConfig newItem) {
            return oldItem.bufferViewName().equals(newItem.bufferViewName());
        }

        @Override
        public boolean areItemsTheSame(QBufferViewConfig item1, QBufferViewConfig item2) {
            return item1.bufferViewId() == item2.bufferViewId();
        }
    });
    private final GeneralCallback<QBufferViewConfig> observer = config -> list.notifyItemChanged(list.indexOf(config));
    Map<Integer, QBufferViewConfig> bufferViewConfigs = new HashMap<>();

    public BufferViewManager(@NonNull List<Integer> bufferViewIds) {
        cachedIds.addAll(bufferViewIds);
    }

    @NonNull
    @Override
    public AndroidObservableSortedList<QBufferViewConfig> bufferViewConfigs() {
        return list;
    }

    @Override
    public QBufferViewConfig bufferViewConfig(int bufferViewId) {
        return bufferViewConfigs.get(bufferViewId);
    }

    public void _addBufferViewConfig(@NonNull QBufferViewConfig config) {
        if (!bufferViewConfigs.containsValue(config)) {
            QBufferViewConfig before = bufferViewConfigs.get(config.bufferViewId());
            if (before != null)
                list.remove(before);

            bufferViewConfigs.put(config.bufferViewId(), config);
            config.init(String.valueOf(config.bufferViewId()), provider, client);
            list.add(config);
            config.addObserver(observer);
            _update();
        }
    }

    @Override
    public void _addBufferViewConfig(int bufferViewConfigId) {
        if (bufferViewConfigs.containsKey(bufferViewConfigId))
            return;

        BufferViewConfig config = BufferViewConfig.create(bufferViewConfigId);
        _addBufferViewConfig(config);
        client.requestInitObject("BufferViewConfig", String.valueOf(bufferViewConfigId));
        _update();
    }

    @Override
    public void _deleteBufferViewConfig(int bufferViewConfigId) {
        if (!bufferViewConfigs.containsKey(bufferViewConfigId))
            return;

        QBufferViewConfig config = bufferViewConfigs.remove(bufferViewConfigId);
        config.deleteObserver(observer);
        list.remove(config);
        _update();
    }

    @Override
    public void checkForNewBuffers(int bufferId) {
        for (QBufferViewConfig config : bufferViewConfigs()) {
            config.checkAddBuffer(bufferId);
        }
    }

    @Override
    public Map<Integer, QBufferViewConfig> bufferViewConfigMap() {
        return bufferViewConfigs;
    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        super.init(objectName, provider, client);
        for (int id : cachedIds) {
            client.requestInitObject("BufferViewConfig", String.valueOf(id));
        }
        client.setBufferViewManager(this);
    }

    @Override
    public void _update(Map<String, QVariant> from) {

    }

    @Override
    public void _update(@NonNull QBufferViewManager from) {
        this.bufferViewConfigs = from.bufferViewConfigMap();
        this.list.retainAll(from.bufferViewConfigs());
        this.list.addAll(from.bufferViewConfigs());
    }
}
