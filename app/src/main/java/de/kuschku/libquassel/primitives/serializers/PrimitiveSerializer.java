package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public interface PrimitiveSerializer<T> {
    void serialize(@NonNull ByteChannel channel, @NonNull T data) throws IOException;

    @Nullable
    T deserialize(@NonNull ByteBuffer buffer) throws IOException;
}
