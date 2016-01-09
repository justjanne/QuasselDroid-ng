package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public interface PrimitiveSerializer<T> {
    void serialize(ByteChannel channel, T data) throws IOException;

    T deserialize(ByteBuffer buffer) throws IOException;
}
