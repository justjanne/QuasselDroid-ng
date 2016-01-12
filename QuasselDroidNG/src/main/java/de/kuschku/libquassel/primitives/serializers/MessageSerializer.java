package de.kuschku.libquassel.primitives.serializers;

import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.kuschku.libquassel.message.Message;

public class MessageSerializer implements PrimitiveSerializer<Message> {
    private static final MessageSerializer serializer = new MessageSerializer();

    private MessageSerializer() {
    }

    public static MessageSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(ByteChannel channel, Message data) throws IOException {
        IntSerializer.get().serialize(channel, data.messageId);
        IntSerializer.get().serialize(channel, (int) (data.time.getMillis() / 1000));
        IntSerializer.get().serialize(channel, data.type.value);
        ByteSerializer.get().serialize(channel, data.flags.flags);
        BufferInfoSerializer.get().serialize(channel, data.bufferInfo);
        ByteArraySerializer.get().serialize(channel, data.sender);
        ByteArraySerializer.get().serialize(channel, data.content);
    }

    @Override
    public Message deserialize(final ByteBuffer buffer) throws IOException {
        return new Message(
                IntSerializer.get().deserialize(buffer),
                new DateTime(((long) IntSerializer.get().deserialize(buffer)) * 1000),
                Message.Type.fromId(IntSerializer.get().deserialize(buffer)),
                new Message.Flags(ByteSerializer.get().deserialize(buffer)),
                BufferInfoSerializer.get().deserialize(buffer),
                ByteArraySerializer.get().deserialize(buffer),
                ByteArraySerializer.get().deserialize(buffer)
        );
    }
}
