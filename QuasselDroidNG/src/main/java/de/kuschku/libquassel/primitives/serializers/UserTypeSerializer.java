package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Map;

import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;

public class UserTypeSerializer<T> implements PrimitiveSerializer<T> {
    private final ObjectSerializer<T> objectSerializer;

    public UserTypeSerializer(ObjectSerializer<T> objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    @Override
    public void serialize(ByteChannel channel, T data) throws IOException {
        VariantSerializer.<Map<String, QVariant>>get().serialize(channel, objectSerializer.toVariantMap(data));
    }

    @Override
    public T deserialize(ByteBuffer buffer) throws IOException {
        return (T) objectSerializer.fromLegacy(((VariantMapSerializer) VariantMapSerializer.get()).deserialize(buffer));
    }
}
