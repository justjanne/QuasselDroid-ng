package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class BoolSerializer implements PrimitiveSerializer<Boolean> {
    private static final BoolSerializer serializer = new BoolSerializer();
    private BoolSerializer() {}
    public static BoolSerializer get(){
        return serializer;
    }

    @Override
    public void serialize(final ByteChannel channel, final Boolean data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(0, (byte) (data ? 0x01 : 0x00));
        channel.write(buffer);
    }

    @Override
    public Boolean deserialize(final ByteBuffer buffer) throws IOException {
        return buffer.get() > 0;
    }
}
