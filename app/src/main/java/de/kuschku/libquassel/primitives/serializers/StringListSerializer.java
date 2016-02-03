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

public class StringListSerializer implements PrimitiveSerializer<List<String>> {
    @NonNull
    private static final StringListSerializer serializer = new StringListSerializer();

    private StringListSerializer() {
    }

    @NonNull
    public static StringListSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final List<String> data) throws IOException {
        IntSerializer.get().serialize(channel, data.size());
        for (String element : data) {
            StringSerializer.get().serialize(channel, element);
        }
    }

    @NonNull
    @Override
    public List<String> deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final int size = IntSerializer.get().deserialize(buffer);
        final List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(StringSerializer.get().deserialize(buffer));
        }
        return list;
    }
}
