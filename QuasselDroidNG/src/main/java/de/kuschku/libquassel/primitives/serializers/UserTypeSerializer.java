package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Map;

import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.*;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class UserTypeSerializer<T> implements PrimitiveSerializer<T> {
    @NonNull
    private final ObjectSerializer<T> objectSerializer;

    public UserTypeSerializer(@NonNull ObjectSerializer<T> objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    @Override
    public void serialize(@NonNull ByteChannel channel, @NonNull T data) throws IOException {
        QVariant<Map<String, QVariant>> variantMap = objectSerializer.toVariantMap(data);
        assertNotNull(variantMap);

        VariantSerializer.<Map<String, QVariant>>get().serialize(channel, variantMap);
    }

    @SuppressWarnings("RedundantCast")
    @NonNull
    @Override
    public T deserialize(@NonNull ByteBuffer buffer) throws IOException {
        return (T)(Object) objectSerializer.fromLegacy(((VariantMapSerializer) VariantMapSerializer.get()).deserialize(buffer));
    }
}
