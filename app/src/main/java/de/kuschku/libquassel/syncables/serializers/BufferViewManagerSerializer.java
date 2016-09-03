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

package de.kuschku.libquassel.syncables.serializers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.impl.BufferViewManager;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewManager;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class BufferViewManagerSerializer implements ObjectSerializer<QBufferViewManager> {
    @NonNull
    private static final BufferViewManagerSerializer serializer = new BufferViewManagerSerializer();

    private BufferViewManagerSerializer() {
    }

    @NonNull
    public static BufferViewManagerSerializer get() {
        return serializer;
    }

    @Nullable
    @Override
    public Map<String, QVariant<Object>> toVariantMap(@NonNull QBufferViewManager data) {
        HashMap<String, QVariant<Object>> map = new HashMap<>();
        map.put("BufferViewIds", new QVariant(getBufferViewIds(data)));
        return map;
    }

    @NonNull
    private List<Integer> getBufferViewIds(@NonNull QBufferViewManager data) {
        List<Integer> bufferViewIds = new ArrayList<>(data.bufferViewConfigs().size());
        for (QBufferViewConfig config : data.bufferViewConfigs())
        bufferViewIds.add(config.bufferViewId());
        return bufferViewIds;
    }

    @NonNull
    @Override
    public QBufferViewManager fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public QBufferViewManager fromLegacy(@NonNull Map<String, QVariant> map) {
        return new BufferViewManager(
                (List<Integer>) map.get("BufferViewIds").data
        );
    }

    @Override
    public QBufferViewManager from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
