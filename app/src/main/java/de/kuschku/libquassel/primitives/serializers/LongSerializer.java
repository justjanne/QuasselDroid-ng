package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class LongSerializer implements PrimitiveSerializer<Long> {
    @Override
    public void serialize(final ByteChannel channel, final Long data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, data);
        channel.write(buffer);
    }

    @Override
    public Long deserialize(final ByteBuffer buffer) throws IOException {
        return buffer.getLong();
    }
}
