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

package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class VariantMapSerializer<T> implements PrimitiveSerializer<Map<String, QVariant<T>>> {
    @NonNull
    private static final VariantMapSerializer serializer = new VariantMapSerializer();
    @NonNull
    private final PrimitiveSerializer<String> stringSerializer = StringSerializer.get();
    @NonNull
    private final VariantSerializer<T> variantSerializer = VariantSerializer.get();

    private VariantMapSerializer() {
    }

    @NonNull
    public static <T> VariantMapSerializer<T> get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final Map<String, QVariant<T>> data) throws IOException {
        IntSerializer.get().serialize(channel, data.size());

        for (Map.Entry<String, QVariant<T>> element : data.entrySet()) {
            stringSerializer.serialize(channel, element.getKey());
            variantSerializer.serialize(channel, element.getValue());
        }
    }

    @NonNull
    @Override
    public Map<String, QVariant<T>> deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final int length = IntSerializer.get().deserialize(buffer);
        final Map<String, QVariant<T>> map = new HashMap<>(length);

        for (int i = 0; i < length; i++) {
            map.put(stringSerializer.deserialize(buffer), variantSerializer.deserialize(buffer));
        }
        return map;
    }
}
