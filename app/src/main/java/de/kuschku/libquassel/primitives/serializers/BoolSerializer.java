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

public class BoolSerializer implements PrimitiveSerializer<Boolean> {
    @NonNull
    private static final BoolSerializer serializer = new BoolSerializer();

    private BoolSerializer() {
    }

    @NonNull
    public static BoolSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final Boolean data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(0, (byte) (data ? 0x01 : 0x00));
        channel.write(buffer);
    }

    @NonNull
    @Override
    public Boolean deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        return buffer.get() > 0;
    }
}
