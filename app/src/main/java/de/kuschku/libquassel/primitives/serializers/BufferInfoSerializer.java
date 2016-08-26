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

import de.kuschku.libquassel.primitives.types.BufferInfo;

public class BufferInfoSerializer implements PrimitiveSerializer<BufferInfo> {
    @NonNull
    private static final BufferInfoSerializer serializer = new BufferInfoSerializer();

    private BufferInfoSerializer() {
    }

    @NonNull
    public static BufferInfoSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull ByteChannel channel, @NonNull BufferInfo data) throws IOException {
        IntSerializer.get().serialize(channel, data.id);
        IntSerializer.get().serialize(channel, data.networkId);
        ShortSerializer.get().serialize(channel, data.type.id);
        IntSerializer.get().serialize(channel, data.groupId);
        ByteArraySerializer.get().serialize(channel, data.name);
    }

    @NonNull
    @Override
    public BufferInfo deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        return BufferInfo.create(
                IntSerializer.get().deserialize(buffer),
                IntSerializer.get().deserialize(buffer),
                BufferInfo.Type.fromId(ShortSerializer.get().deserialize(buffer)),
                IntSerializer.get().deserialize(buffer),
                ByteArraySerializer.get().deserialize(buffer)
        );
    }
}
