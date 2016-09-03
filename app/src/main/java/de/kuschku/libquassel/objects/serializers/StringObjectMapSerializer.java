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

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class StringObjectMapSerializer<T> implements ObjectSerializer<Map<String, T>> {
    @NonNull
    private static final StringObjectMapSerializer serializer = new StringObjectMapSerializer();

    private StringObjectMapSerializer() {
    }

    @NonNull
    public static <T> StringObjectMapSerializer<T> get() {
        return serializer;
    }

    @NonNull
    @Override
    public Map<String, QVariant<Object>> toVariantMap(@NonNull Map<String, T> data) {
        final Map<String, QVariant<Object>> map = new HashMap<>();

        for (Map.Entry<String, T> entry : data.entrySet()) {
            map.put(entry.getKey(), new QVariant<>(entry.getValue()));
        }
        return map;
    }

    @NonNull
    @Override
    public Map<String, T> fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public Map<String, T> fromLegacy(@NonNull Map<String, QVariant> map) {
        final HashMap<String, T> result = new HashMap<>();
        for (Map.Entry<String, QVariant> entry : map.entrySet()) {
            result.put(entry.getKey(), (T) entry.getValue().get());
        }
        return result;
    }

    @Override
    public Map<String, T> from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
