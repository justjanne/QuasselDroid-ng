package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.kuschku.libquassel.primitives.types.BufferInfo;

public class BufferInfoSerializer implements PrimitiveSerializer<BufferInfo> {
    @Override
    public void serialize(ByteChannel channel, BufferInfo data) throws IOException {
        new IntSerializer().serialize(channel, data.id);
        new IntSerializer().serialize(channel, data.networkId);
        new ShortSerializer().serialize(channel, data.type.id);
        new IntSerializer().serialize(channel, data.groupId);
        new ByteArraySerializer().serialize(channel, data.name);
    }

    @Override
    public BufferInfo deserialize(final ByteBuffer buffer) throws IOException {
        return new BufferInfo(
                new IntSerializer().deserialize(buffer),
                new IntSerializer().deserialize(buffer),
                BufferInfo.Type.fromId(new ShortSerializer().deserialize(buffer)),
                new IntSerializer().deserialize(buffer),
                new ByteArraySerializer().deserialize(buffer)
        );
    }
}
