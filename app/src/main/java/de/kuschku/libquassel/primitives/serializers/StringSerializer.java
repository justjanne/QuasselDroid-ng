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
import android.support.annotation.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;

public class StringSerializer implements PrimitiveSerializer<String> {
    @NonNull
    private static final StringSerializer serializer = new StringSerializer();

    private StringSerializer() {
    }

    @NonNull
    public static StringSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @Nullable final String data) throws IOException {
        if (data == null) {
            IntSerializer.get().serialize(channel, 0xffffffff);
        } else {
            final ByteBuffer contentBuffer = Charset.forName("UTF-16BE").encode(data);
            IntSerializer.get().serialize(channel, contentBuffer.limit());
            channel.write(contentBuffer);
        }
    }

    @Nullable
    @Override
    public String deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final int len = IntSerializer.get().deserialize(buffer);
        if (len == 0xffffffff)
            return null;
        else {
            final ByteBuffer contentBuffer = ByteBuffer.allocate(len);
            contentBuffer.put(buffer.array(), buffer.position(), len);
            contentBuffer.position(0);
            buffer.position(buffer.position() + len);
            return Charset.forName("UTF-16BE").decode(contentBuffer).toString();
        }
    }
}
