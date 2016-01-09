package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class IntSerializer implements PrimitiveSerializer<Integer> {
    @Override
    public void serialize(final ByteChannel channel, final Integer data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(0, data);
        channel.write(buffer);
    }

    @Override
    public Integer deserialize(final ByteBuffer buffer) throws IOException {
        return buffer.getInt();
    }
}
