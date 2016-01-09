package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.primitives.types.QVariant;

public class VariantListSerializer<T> implements PrimitiveSerializer<List<T>> {
    @Override
    public void serialize(final ByteChannel channel, final List<T> data) throws IOException {
        new IntSerializer().serialize(channel, data.size());

        final VariantSerializer<T> variantSerializer = new VariantSerializer<>();
        for (T element : data) {
            variantSerializer.serialize(channel, new QVariant<>(element));
        }
    }

    @Override
    public List<T> deserialize(final ByteBuffer buffer) throws IOException {
        final int length = new IntSerializer().deserialize(buffer);
        final List<T> list = new ArrayList<>(length);

        final VariantSerializer<T> variantSerializer = new VariantSerializer<>();
        for (int i = 0; i < length; i++) {
            list.add(variantSerializer.deserialize(buffer).data);
        }
        return list;
    }
}
