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

import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.BufferInfo;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class MessageSerializer implements PrimitiveSerializer<Message> {
    @NonNull
    private static final MessageSerializer serializer = new MessageSerializer();

    private MessageSerializer() {
    }

    @NonNull
    public static MessageSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull ByteChannel channel, @NonNull Message data) throws IOException {
        IntSerializer.get().serialize(channel, data.messageId);
        IntSerializer.get().serialize(channel, (int) (data.time.getMillis() / 1000));
        IntSerializer.get().serialize(channel, data.type.value);
        ByteSerializer.get().serialize(channel, data.flags.flags);
        BufferInfoSerializer.get().serialize(channel, data.bufferInfo);
        ByteArraySerializer.get().serialize(channel, data.sender);
        ByteArraySerializer.get().serialize(channel, data.content);
    }

    @Nullable
    @Override
    public Message deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        Integer messageId = IntSerializer.get().deserialize(buffer);
        DateTime time = new DateTime(((long) IntSerializer.get().deserialize(buffer)) * 1000);
        Message.Type type = Message.Type.fromId(IntSerializer.get().deserialize(buffer));
        Message.Flags flags = new Message.Flags(ByteSerializer.get().deserialize(buffer));
        BufferInfo bufferInfo = BufferInfoSerializer.get().deserialize(buffer);
        String sender = ByteArraySerializer.get().deserialize(buffer);
        String message = ByteArraySerializer.get().deserialize(buffer);

        assertNotNull(sender);
        assertNotNull(message);
        return new Message(
                messageId,
                time,
                type,
                flags,
                bufferInfo,
                sender,
                message
        );
    }
}
