package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.kuschku.libquassel.message.Message;

import static de.kuschku.util.AndroidAssert.*;

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
        String sender = ByteArraySerializer.get().deserialize(buffer);
        String message = ByteArraySerializer.get().deserialize(buffer);

        assertNotNull(sender);
        assertNotNull(message);

        return new Message(
                IntSerializer.get().deserialize(buffer),
                new DateTime(((long) IntSerializer.get().deserialize(buffer)) * 1000),
                Message.Type.fromId(IntSerializer.get().deserialize(buffer)),
                new Message.Flags(ByteSerializer.get().deserialize(buffer)),
                BufferInfoSerializer.get().deserialize(buffer),
                sender,
                message
        );
    }
}
