package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.kuschku.libquassel.primitives.types.BufferInfo;

public class BufferInfoSerializer implements PrimitiveSerializer<BufferInfo> {
    private static final BufferInfoSerializer serializer = new BufferInfoSerializer();
    private BufferInfoSerializer() {}
    public static BufferInfoSerializer get(){
        return serializer;
    }

    @Override
    public void serialize(ByteChannel channel, BufferInfo data) throws IOException {
        IntSerializer.get().serialize(channel, data.id);
        IntSerializer.get().serialize(channel, data.networkId);
        ShortSerializer.get().serialize(channel, data.type.id);
        IntSerializer.get().serialize(channel, data.groupId);
        ByteArraySerializer.get().serialize(channel, data.name);
    }

    @Override
    public BufferInfo deserialize(final ByteBuffer buffer) throws IOException {
        return new BufferInfo(
                IntSerializer.get().deserialize(buffer),
                IntSerializer.get().deserialize(buffer),
                BufferInfo.Type.fromId(ShortSerializer.get().deserialize(buffer)),
                IntSerializer.get().deserialize(buffer),
                ByteArraySerializer.get().deserialize(buffer)
        );
    }
}
