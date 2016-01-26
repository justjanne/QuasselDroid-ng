package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;

public class CharSerializer implements PrimitiveSerializer<Character> {
    @NonNull
    private static final CharSerializer serializer = new CharSerializer();

    private CharSerializer() {
    }

    @NonNull
    public static CharSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final Character data) throws IOException {
        final ByteBuffer buffer = Charset.forName("UTF-16BE").encode(String.valueOf(data.charValue()));
        channel.write(buffer);
    }

    @NonNull
    @Override
    public Character deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final ByteBuffer contentBuffer = ByteBuffer.allocate(2);
        contentBuffer.put(buffer.array(), buffer.position(), 2);
        buffer.position(buffer.position() + 2);
        return Charset.forName("UTF-16BE").decode(buffer).get();
    }
}
