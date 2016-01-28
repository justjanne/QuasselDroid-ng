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
        final ByteBuffer contentBuffer = Charset.forName("UTF-16BE").encode(String.copyValueOf(new char[]{data}));
        channel.write(contentBuffer);
    }

    @NonNull
    @Override
    public Character deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        int len = 2;
        final ByteBuffer contentBuffer = ByteBuffer.allocate(len);
        contentBuffer.put(buffer.array(), buffer.position(), len);
        contentBuffer.position(0);
        buffer.position(buffer.position() + len);
        return Charset.forName("UTF-16BE").decode(contentBuffer).toString().charAt(0);
    }
}
