package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class BoolSerializer implements PrimitiveSerializer<Boolean> {
    @NonNull
    private static final BoolSerializer serializer = new BoolSerializer();
    private BoolSerializer() {}
    @NonNull
    public static BoolSerializer get(){
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final Boolean data) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(0, (byte) (data ? 0x01 : 0x00));
        channel.write(buffer);
    }

    @NonNull
    @Override
    public Boolean deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        return buffer.get() > 0;
    }
}
