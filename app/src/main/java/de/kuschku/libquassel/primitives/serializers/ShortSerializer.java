package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ShortSerializer implements PrimitiveSerializer<Short> {
    @NonNull
    private static final ShortSerializer serializer = new ShortSerializer();

    private ShortSerializer() {
    }

    @NonNull
    public static ShortSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final Short data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(0, data);
        channel.write(buffer);
    }

    @NonNull
    @Override
    public Short deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        return buffer.getShort();
    }
}
