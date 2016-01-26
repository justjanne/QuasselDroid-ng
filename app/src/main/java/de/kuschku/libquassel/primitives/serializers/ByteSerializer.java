package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ByteSerializer implements PrimitiveSerializer<Byte> {
    @NonNull
    private static final ByteSerializer serializer = new ByteSerializer();

    private ByteSerializer() {
    }

    @NonNull
    public static ByteSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final Byte data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(0, data);
        channel.write(buffer);
    }

    @NonNull
    @Override
    public Byte deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        return buffer.get();
    }
}
