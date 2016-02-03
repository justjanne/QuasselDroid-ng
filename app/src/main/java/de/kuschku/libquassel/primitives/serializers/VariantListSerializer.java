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

package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class VariantListSerializer<T> implements PrimitiveSerializer<List<T>> {
    @NonNull
    private static final VariantListSerializer serializer = new VariantListSerializer();

    private VariantListSerializer() {
    }

    @NonNull
    public static <T> VariantListSerializer<T> get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final List<T> data) throws IOException {
        IntSerializer.get().serialize(channel, data.size());

        final VariantSerializer<T> variantSerializer = VariantSerializer.get();
        for (T element : data) {
            variantSerializer.serialize(channel, new QVariant<>(element));
        }
    }

    @NonNull
    @Override
    public List<T> deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final int length = IntSerializer.get().deserialize(buffer);
        final List<T> list = new ArrayList<>(length);

        final VariantSerializer<T> variantSerializer = VariantSerializer.get();
        for (int i = 0; i < length; i++) {
            list.add(variantSerializer.deserialize(buffer).data);
        }
        return list;
    }
}
