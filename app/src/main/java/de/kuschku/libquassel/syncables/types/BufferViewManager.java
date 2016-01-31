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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.BufferViewManagerSerializer;

public class BufferViewManager extends SyncableObject<BufferViewManager> {
    @NonNull
    public Map<Integer, BufferViewConfig> BufferViews = new HashMap<>();
    private Client client;

    public BufferViewManager(@NonNull List<Integer> BufferViewIds) {
        for (int i : BufferViewIds) {
            BufferViews.put(i, null);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "BufferViewManager{" +
                "BufferViews=" + BufferViews +
                '}';
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {
        this.client = client;
        setObjectName(function.objectName);
        client.setBufferViewManager(this);
    }

    @Override
    public void update(@NonNull BufferViewManager from) {
        this.BufferViews = from.BufferViews;
        for (int id : BufferViews.keySet()) {
            client.sendInitRequest("BufferViewConfig", String.valueOf(id));
        }
    }

    @Override
    public void update(@NonNull Map<String, QVariant> from) {
        update(BufferViewManagerSerializer.get().fromDatastream(from));
    }
}
