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

import de.kuschku.libquassel.client.FeatureFlags;
import de.kuschku.libquassel.primitives.types.Protocol;

public class ProtocolSerializer implements PrimitiveSerializer<Protocol> {
    @NonNull
    private static final ProtocolSerializer serializer = new ProtocolSerializer();

    private ProtocolSerializer() {
    }

    @NonNull
    public static ProtocolSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull ByteChannel channel, @NonNull Protocol data) throws IOException {
        ByteSerializer.get().serialize(channel, data.protocolFlags.flags);
        ShortSerializer.get().serialize(channel, data.protocolData);
        ByteSerializer.get().serialize(channel, data.protocolVersion);
    }

    @NonNull
    @Override
    public Protocol deserialize(@NonNull ByteBuffer buffer) throws IOException {
        return new Protocol(
                new FeatureFlags(ByteSerializer.get().deserialize(buffer)),
                ShortSerializer.get().deserialize(buffer),
                ByteSerializer.get().deserialize(buffer)
        );
    }
}
