package de.kuschku.libquassel.primitives.serializers;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;

public class StringSerializer implements PrimitiveSerializer<String> {
    @Override
    public void serialize(@NonNull final ByteChannel channel, @Nullable final String data) throws IOException {
        if (data == null) {
            new IntSerializer().serialize(channel, 0xffffffff);
        } else {
            final ByteBuffer contentBuffer = Charset.forName("UTF-16BE").encode(data);
            new IntSerializer().serialize(channel, contentBuffer.limit());
            channel.write(contentBuffer);
        }
    }

    @Nullable
    @Override
    public String deserialize(final ByteBuffer buffer) throws IOException {
        final int len = new IntSerializer().deserialize(buffer);
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
