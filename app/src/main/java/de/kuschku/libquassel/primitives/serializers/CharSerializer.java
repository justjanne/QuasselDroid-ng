package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;

public class CharSerializer implements PrimitiveSerializer<Character> {
    @Override
    public void serialize(final ByteChannel channel, final Character data) throws IOException {
        final ByteBuffer buffer = Charset.forName("UTF-16BE").encode(String.valueOf(data.charValue()));
        channel.write(buffer);
    }

    @Override
    public Character deserialize(final ByteBuffer buffer) throws IOException {
        final ByteBuffer contentBuffer = ByteBuffer.allocate(2);
        contentBuffer.put(buffer.array(), buffer.position(), 2);
        buffer.position(buffer.position() + 2);
        return Charset.forName("UTF-16BE").decode(buffer).get();
    }
}
