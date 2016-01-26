package de.kuschku.libquassel.primitives.serializers;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class VoidSerializer implements PrimitiveSerializer<Void> {
    @NonNull
    private static final VoidSerializer serializer = new VoidSerializer();

    private VoidSerializer() {
    }

    @NonNull
    public static VoidSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final Void data) throws IOException {

    }

    @Nullable
    @Override
    public Void deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        return null;
    }
}
