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

import com.google.common.base.Charsets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ByteArraySerializer implements PrimitiveSerializer<String> {
    @NonNull
    private static final ByteArraySerializer serializerFalse = new ByteArraySerializer(false);
    @NonNull
    private static final ByteArraySerializer serializerTrue = new ByteArraySerializer(true);
    private final boolean trimLastByte;

    private ByteArraySerializer(boolean trimLastByte) {
        this.trimLastByte = trimLastByte;
    }

    @NonNull
    public static ByteArraySerializer get() {
        return get(false);
    }

    @NonNull
    public static ByteArraySerializer get(boolean trimLastByte) {
        if (trimLastByte) return serializerTrue;
        else return serializerFalse;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @Nullable final String data) throws IOException {
        if (data == null) {
            IntSerializer.get().serialize(channel, 0xffffffff);
        } else {
            final ByteBuffer contentBuffer = Charsets.ISO_8859_1.encode(data);
            IntSerializer.get().serialize(channel, contentBuffer.limit() + (trimLastByte ? 1 : 0));
            channel.write(contentBuffer);
            if (trimLastByte) channel.write(ByteBuffer.allocate(1));
        }
    }

    @Nullable
    @Override
    public String deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final int len = IntSerializer.get().deserialize(buffer);
        if (len == 0xffffffff)
            return null;
        else if (len == 0)
            return "";
        else {
            final ByteBuffer contentBuffer = ByteBuffer.allocate(len);
            contentBuffer.put(buffer.array(), buffer.position(), len);
            contentBuffer.position(0);
            buffer.position(buffer.position() + len);

            // We have to do this for the usecase of usertype names, as those are serialized with \0 at the end
            if (trimLastByte) {
                // Get rid of the null byte at the end
                contentBuffer.limit(len - 1);
            }
            return Charsets.UTF_8.decode(contentBuffer).toString();
        }
    }
}
