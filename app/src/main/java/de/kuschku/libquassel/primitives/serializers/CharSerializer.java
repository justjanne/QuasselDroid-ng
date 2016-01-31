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
import java.nio.charset.Charset;

public class CharSerializer implements PrimitiveSerializer<Character> {
    @NonNull
    private static final CharSerializer serializer = new CharSerializer();

    private CharSerializer() {
    }

    @NonNull
    public static CharSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final Character data) throws IOException {
        final ByteBuffer contentBuffer = Charset.forName("UTF-16BE").encode(String.copyValueOf(new char[]{data}));
        channel.write(contentBuffer);
    }

    @NonNull
    @Override
    public Character deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        int len = 2;
        final ByteBuffer contentBuffer = ByteBuffer.allocate(len);
        contentBuffer.put(buffer.array(), buffer.position(), len);
        contentBuffer.position(0);
        buffer.position(buffer.position() + len);
        return Charset.forName("UTF-16BE").decode(contentBuffer).toString().charAt(0);
    }
}
