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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.impl.BufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class BufferViewConfigSerializer implements ObjectSerializer<BufferViewConfig> {
    @NonNull
    private static final BufferViewConfigSerializer serializer = new BufferViewConfigSerializer();

    private BufferViewConfigSerializer() {
    }

    @NonNull
    public static BufferViewConfigSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public Map<String, QVariant<Object>> toVariantMap(@NonNull BufferViewConfig data) {
        final Map<String, QVariant<Object>> map = new HashMap<>();
        map.put("bufferViewName", new QVariant<>(data.bufferViewName()));
        map.put("TemporarilyRemovedBuffers", new QVariant<>(data.temporarilyRemovedBuffers()));
        map.put("hideInactiveNetworks", new QVariant<>(data.hideInactiveBuffers()));
        map.put("BufferList", new QVariant<>(data.bufferList()));
        map.put("allowedBufferTypes", new QVariant<>(data.allowedBufferTypes()));
        map.put("sortAlphabetically", new QVariant<>(data.sortAlphabetically()));
        map.put("disableDecoration", new QVariant<>(data.disableDecoration()));
        map.put("addNewBuffersAutomatically", new QVariant<>(data.addNewBuffersAutomatically()));
        map.put("networkId", new QVariant<>("NetworkId", data.networkId()));
        map.put("minimumActivity", new QVariant<>(data.minimumActivity().id));
        map.put("hideInactiveBuffers", new QVariant<>(data.hideInactiveBuffers()));
        map.put("RemovedBuffers", new QVariant<>(data.removedBuffers()));
        return map;
    }

    @NonNull
    @Override
    public BufferViewConfig fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public BufferViewConfig fromLegacy(@NonNull Map<String, QVariant> map) {
        return new BufferViewConfig(
                (String) map.get("bufferViewName").data,
                (List<Integer>) map.get("TemporarilyRemovedBuffers").data,
                (boolean) map.get("hideInactiveNetworks").data,
                (List<Integer>) map.get("BufferList").data,
                (int) map.get("allowedBufferTypes").data,
                (boolean) map.get("sortAlphabetically").data,
                (boolean) map.get("disableDecoration").data,
                (boolean) map.get("addNewBuffersAutomatically").data,
                (int) map.get("networkId").data,
                QBufferViewConfig.MinimumActivity.fromId((int) map.get("minimumActivity").data),
                (boolean) map.get("hideInactiveBuffers").data,
                (List<Integer>) map.get("RemovedBuffers").data
        );
    }

    @Override
    public BufferViewConfig from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
