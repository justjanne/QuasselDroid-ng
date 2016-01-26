package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;

public class StringListSerializer implements PrimitiveSerializer<List<String>> {
    @NonNull
    private static final StringListSerializer serializer = new StringListSerializer();

    private StringListSerializer() {
    }

    @NonNull
    public static StringListSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final List<String> data) throws IOException {
        IntSerializer.get().serialize(channel, data.size());
        for (String element : data) {
            StringSerializer.get().serialize(channel, element);
        }
    }

    @NonNull
    @Override
    public List<String> deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final int size = IntSerializer.get().deserialize(buffer);
        final List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(StringSerializer.get().deserialize(buffer));
        }
        return list;
    }
}
