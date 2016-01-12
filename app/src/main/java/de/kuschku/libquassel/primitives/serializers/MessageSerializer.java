package de.kuschku.libquassel.primitives.serializers;

import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.kuschku.libquassel.message.Message;

public class MessageSerializer implements PrimitiveSerializer<Message> {
    @Override
    public void serialize(ByteChannel channel, Message data) throws IOException {
        new IntSerializer().serialize(channel, data.messageId);
        new IntSerializer().serialize(channel, (int) (data.time.getMillis() / 1000));
        new IntSerializer().serialize(channel, data.type.value);
        new ByteSerializer().serialize(channel, data.flags.flags);
        new BufferInfoSerializer().serialize(channel, data.bufferInfo);
        new ByteArraySerializer().serialize(channel, data.sender);
        new ByteArraySerializer().serialize(channel, data.content);
    }

    @Override
    public Message deserialize(final ByteBuffer buffer) throws IOException {
        return new Message(
                new IntSerializer().deserialize(buffer),
                new DateTime(((long) new IntSerializer().deserialize(buffer)) * 1000),
                Message.Type.fromId(new IntSerializer().deserialize(buffer)),
                new Message.Flags(new ByteSerializer().deserialize(buffer)),
                new BufferInfoSerializer().deserialize(buffer),
                new ByteArraySerializer().deserialize(buffer),
                new ByteArraySerializer().deserialize(buffer)
        );
    }
}
