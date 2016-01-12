package de.kuschku.libquassel.primitives.serializers;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;

public class StringSerializer implements PrimitiveSerializer<String> {
    private static final StringSerializer serializer = new StringSerializer();

    private StringSerializer() {
    }

    public static StringSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @Nullable final String data) throws IOException {
        if (data == null) {
            IntSerializer.get().serialize(channel, 0xffffffff);
        } else {
            final ByteBuffer contentBuffer = Charset.forName("UTF-16BE").encode(data);
            IntSerializer.get().serialize(channel, contentBuffer.limit());
            channel.write(contentBuffer);
        }
    }

    @Nullable
    @Override
    public String deserialize(final ByteBuffer buffer) throws IOException {
        final int len = IntSerializer.get().deserialize(buffer);
        if (len == 0xffffffff)
            return null;
        else {
            final ByteBuffer contentBuffer = ByteBuffer.allocate(len);
            contentBuffer.put(buffer.array(), buffer.position(), len);
            contentBuffer.position(0);
            buffer.position(buffer.position() + len);
            return Charset.forName("UTF-16BE").decode(contentBuffer).toString();
        }
    }
}
