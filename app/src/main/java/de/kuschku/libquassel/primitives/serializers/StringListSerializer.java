package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;

public class StringListSerializer implements PrimitiveSerializer<List<String>> {
    @Override
    public void serialize(final ByteChannel channel, final List<String> data) throws IOException {
        new IntSerializer().serialize(channel, data.size());
        for (String element : data) {
            new StringSerializer().serialize(channel, element);
        }
    }

    @Override
    public List<String> deserialize(final ByteBuffer buffer) throws IOException {
        final int size = new IntSerializer().deserialize(buffer);
        final List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(new StringSerializer().deserialize(buffer));
        }
        return list;
    }
}
