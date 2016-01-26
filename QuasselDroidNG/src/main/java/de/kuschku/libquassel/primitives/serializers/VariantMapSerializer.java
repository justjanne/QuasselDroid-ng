package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class VariantMapSerializer<T> implements PrimitiveSerializer<Map<String, QVariant<T>>> {
    @NonNull
    private static final VariantMapSerializer serializer = new VariantMapSerializer();

    private VariantMapSerializer() {
    }

    @NonNull
    public static <T> VariantMapSerializer<T> get() {
        return serializer;
    }

    @NonNull
    private final PrimitiveSerializer<String> stringSerializer = StringSerializer.get();
    @NonNull
    private final VariantSerializer<T> variantSerializer = VariantSerializer.get();

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final Map<String, QVariant<T>> data) throws IOException {
        IntSerializer.get().serialize(channel, data.size());

        for (Map.Entry<String, QVariant<T>> element : data.entrySet()) {
            stringSerializer.serialize(channel, element.getKey());
            variantSerializer.serialize(channel, element.getValue());
        }
    }

    @NonNull
    @Override
    public Map<String, QVariant<T>> deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final int length = IntSerializer.get().deserialize(buffer);
        final Map<String, QVariant<T>> map = new HashMap<>(length);

        for (int i = 0; i < length; i++) {
            map.put(stringSerializer.deserialize(buffer), variantSerializer.deserialize(buffer));
        }
        return map;
    }
}
