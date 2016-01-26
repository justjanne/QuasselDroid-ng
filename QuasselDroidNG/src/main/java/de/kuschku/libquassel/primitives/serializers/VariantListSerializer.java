package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class VariantListSerializer<T> implements PrimitiveSerializer<List<T>> {
    @NonNull
    private static final VariantListSerializer serializer = new VariantListSerializer();

    private VariantListSerializer() {
    }

    @NonNull
    public static <T> VariantListSerializer<T> get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final List<T> data) throws IOException {
        IntSerializer.get().serialize(channel, data.size());

        final VariantSerializer<T> variantSerializer = VariantSerializer.get();
        for (T element : data) {
            variantSerializer.serialize(channel, new QVariant<>(element));
        }
    }

    @NonNull
    @Override
    public List<T> deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final int length = IntSerializer.get().deserialize(buffer);
        final List<T> list = new ArrayList<>(length);

        final VariantSerializer<T> variantSerializer = VariantSerializer.get();
        for (int i = 0; i < length; i++) {
            list.add(variantSerializer.deserialize(buffer).data);
        }
        return list;
    }
}
