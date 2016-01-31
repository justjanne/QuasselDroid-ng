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
import de.kuschku.libquassel.syncables.types.BufferViewConfig;

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
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull BufferViewConfig data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("bufferViewName", new QVariant<>(data.getBufferViewName()));
        map.data.put("TemporarilyRemovedBuffers", new QVariant<>(data.getTemporarilyRemovedBuffers()));
        map.data.put("hideInactiveNetworks", new QVariant<>(data.isHideInactiveNetworks()));
        map.data.put("BufferList", new QVariant<>(data.getBuffers()));
        map.data.put("allowedBufferTypes", new QVariant<>(data.getAllowedBufferTypes()));
        map.data.put("sortAlphabetically", new QVariant<>(data.isSortAlphabetically()));
        map.data.put("disableDecoration", new QVariant<>(data.isDisableDecoration()));
        map.data.put("addNewBuffersAutomatically", new QVariant<>(data.isAddNewBuffersAutomatically()));
        map.data.put("networkId", new QVariant<>(data.getNetworkId()));
        map.data.put("minimumActivity", new QVariant<>(data.getMinimumActivity()));
        map.data.put("hideInactiveBuffers", new QVariant<>(data.isHideInactiveBuffers()));
        map.data.put("RemovedBuffers", new QVariant<>(data.getRemovedBuffers()));
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
                (int) map.get("minimumActivity").data,
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
