package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class VariantVariantListSerializer<T> implements PrimitiveSerializer<List<QVariant<T>>> {
    @NonNull
    private static final VariantVariantListSerializer serializer = new VariantVariantListSerializer();

    private VariantVariantListSerializer() {
    }

    @NonNull
    public static <T> VariantVariantListSerializer<T> get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final List<QVariant<T>> data) throws IOException {
        IntSerializer.get().serialize(channel, data.size());

        final VariantSerializer<T> variantSerializer = VariantSerializer.get();
        for (QVariant<T> element : data) {
            variantSerializer.serialize(channel, element);
        }
    }

    @NonNull
    @Override
    public List<QVariant<T>> deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final int length = IntSerializer.get().deserialize(buffer);
        final List<QVariant<T>> list = new ArrayList<>(length);

        final VariantSerializer<T> variantSerializer = VariantSerializer.get();
        for (int i = 0; i < length; i++) {
            list.add(variantSerializer.deserialize(buffer));
        }
        return list;
    }
}
