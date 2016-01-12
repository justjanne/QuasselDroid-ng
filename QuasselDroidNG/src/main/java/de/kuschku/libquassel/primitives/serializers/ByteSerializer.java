package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ByteSerializer implements PrimitiveSerializer<Byte> {
    private static final ByteSerializer serializer = new ByteSerializer();

    private ByteSerializer() {
    }

    public static ByteSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(final ByteChannel channel, final Byte data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(0, data);
        channel.write(buffer);
    }

    @Override
    public Byte deserialize(final ByteBuffer buffer) throws IOException {
        return buffer.get();
    }
}
