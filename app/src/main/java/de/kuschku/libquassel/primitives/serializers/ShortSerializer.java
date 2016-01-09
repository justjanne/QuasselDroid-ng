package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ShortSerializer implements PrimitiveSerializer<Short> {
    @Override
    public void serialize(final ByteChannel channel, final Short data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(0, data);
        channel.write(buffer);
    }

    @Override
    public Short deserialize(final ByteBuffer buffer) throws IOException {
        return buffer.getShort();
    }
}
