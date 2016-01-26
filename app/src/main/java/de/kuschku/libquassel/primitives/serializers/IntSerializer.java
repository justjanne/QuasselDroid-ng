package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class IntSerializer implements PrimitiveSerializer<Integer> {
    @NonNull
    private static final IntSerializer serializer = new IntSerializer();

    private IntSerializer() {
    }

    @NonNull
    public static IntSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final Integer data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(0, data);
        channel.write(buffer);
    }

    @NonNull
    @Override
    public Integer deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        return buffer.getInt();
    }
}
