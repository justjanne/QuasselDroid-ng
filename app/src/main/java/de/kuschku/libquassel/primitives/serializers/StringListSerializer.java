package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;

public class StringListSerializer implements PrimitiveSerializer<List<String>> {
    private static final StringListSerializer serializer = new StringListSerializer();

    private StringListSerializer() {
    }

    public static StringListSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(final ByteChannel channel, final List<String> data) throws IOException {
        IntSerializer.get().serialize(channel, data.size());
        for (String element : data) {
            StringSerializer.get().serialize(channel, element);
        }
    }

    @Override
    public List<String> deserialize(final ByteBuffer buffer) throws IOException {
        final int size = IntSerializer.get().deserialize(buffer);
        final List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(StringSerializer.get().deserialize(buffer));
        }
        return list;
    }
}
