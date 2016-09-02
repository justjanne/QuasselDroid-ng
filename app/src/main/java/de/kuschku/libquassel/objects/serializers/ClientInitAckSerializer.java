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

package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientInitAck;
import de.kuschku.libquassel.objects.types.StorageBackend;
import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class ClientInitAckSerializer implements ObjectSerializer<ClientInitAck> {
    @NonNull
    private static final ClientInitAckSerializer serializer = new ClientInitAckSerializer();

    private ClientInitAckSerializer() {
    }

    @NonNull
    public static ClientInitAckSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public Map<String, QVariant<Object>> toVariantMap(@NonNull final ClientInitAck data) {
        final List<Map<String, QVariant<Object>>> storageBackends = new ArrayList<>();
        final StorageBackendSerializer storageBackendSerializer = StorageBackendSerializer.get();
        if (data.StorageBackends != null)
            for (StorageBackend backend : data.StorageBackends) {
                storageBackends.add(storageBackendSerializer.toVariantMap(backend));
            }

        final Map<String, QVariant<Object>> map = new HashMap<>();
        map.put("Configured", new QVariant<>(data.Configured));
        map.put("LoginEnabled", new QVariant<>(data.LoginEnabled));
        map.put("StorageBackends", new QVariant<>(storageBackends));
        map.put("CoreFeatures", new QVariant<>(data.CoreFeatures));
        return map;
    }

    @NonNull
    @Override
    public ClientInitAck fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public ClientInitAck fromLegacy(@NonNull Map<String, QVariant> map) {
        final List<StorageBackend> storageBackends = new ArrayList<>();
        if (map.containsKey("StorageBackends")) {
            final StorageBackendSerializer storageBackendSerializer = StorageBackendSerializer.get();
            for (Map<String, QVariant> backend : (List<Map<String, QVariant>>) map.get("StorageBackends").data) {
                storageBackends.add(storageBackendSerializer.fromLegacy(backend));
            }
        }
        final int coreFeatures = map.containsKey("CoreFeatures") ? ((QVariant<Integer>) map.get("CoreFeatures")).data : 0x00;
        return new ClientInitAck(
                ((QVariant<Boolean>) map.get("Configured")).data,
                ((QVariant<Boolean>) map.get("LoginEnabled")).data,
                coreFeatures,
                storageBackends
        );
    }

    @Nullable
    @Override
    public ClientInitAck from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
