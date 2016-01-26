package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class LongSerializer implements PrimitiveSerializer<Long> {
    @NonNull
    private static final LongSerializer serializer = new LongSerializer();

    private LongSerializer() {
    }

    @NonNull
    public static LongSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final Long data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, data);
        channel.write(buffer);
    }

    @NonNull
    @Override
    public Long deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        return buffer.getLong();
    }
}
