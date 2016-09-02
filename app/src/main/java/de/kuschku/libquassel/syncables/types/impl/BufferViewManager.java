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

import java.util.ArrayList;
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

public class BufferViewManager extends ABufferViewManager<BufferViewManager> {
    @NonNull
    final
    Set<Integer> cachedIds = new HashSet<>();
    Map<Integer, QBufferViewConfig> bufferViewConfigs = new HashMap<>();

    public BufferViewManager(@NonNull List<Integer> bufferViewIds) {
        cachedIds.addAll(bufferViewIds);
    }

    @NonNull
    @Override
    public List<QBufferViewConfig> bufferViewConfigs() {
        return new ArrayList<>(bufferViewConfigs.values());
    }

    @Override
    public QBufferViewConfig bufferViewConfig(int bufferViewId) {
        return bufferViewConfigs.get(bufferViewId);
    }

    public void _addBufferViewConfig(@NonNull QBufferViewConfig config) {
        if (!bufferViewConfigs.containsValue(config)) {

            bufferViewConfigs.put(config.bufferViewId(), config);
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

        bufferViewConfigs.remove(bufferViewConfigId);
        _update();
    }

    @Override
    public void checkForNewBuffers(int bufferId) {
        for (QBufferViewConfig config : bufferViewConfigs()) {
            config.checkAddBuffer(bufferId);
        }
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
    public void _update(@NonNull BufferViewManager from) {
        this.bufferViewConfigs = from.bufferViewConfigs;
    }
}
