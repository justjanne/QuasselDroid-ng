package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.QMetaTypeRegistry;
import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class VariantSerializer<T> implements PrimitiveSerializer<QVariant<T>> {
    @NonNull
    private static final VariantSerializer serializer = new VariantSerializer();

    private VariantSerializer() {
    }

    @NonNull
    public static <T> VariantSerializer<T> get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final QVariant<T> data) throws IOException {
        IntSerializer.get().serialize(channel, data.type.type.getValue());
        BoolSerializer.get().serialize(channel, data.data == null);
        if (data.type.type == QMetaType.Type.UserType) {
            ByteArraySerializer.get(true).serialize(channel, data.type.name);
        }
        if (data.type.serializer == null) {
            throw new IOException("Unknown type: " + data.type.name);
        }
        data.type.serializer.serialize(channel, data.data);
    }

    @NonNull
    @Override
    public QVariant<T> deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        // Read original type
        final QMetaType.Type type = QMetaType.Type.fromId(IntSerializer.get().deserialize(buffer));

        // Read if the data is defined or null
        // TODO: For some reason, this is completely ignored. Figure out why and document.
        final boolean isNull = BoolSerializer.get().deserialize(buffer);

        // Get the actual serialized type
        final QMetaType<T> mtype;
        if (type == QMetaType.Type.UserType) {
            // If the type is a user-defined type, read type name
            // WARNING: This ByteArray has a trailing null byte, which we can’t deserialize.
            //          Therefore we have to pass a flag to make sure the serializer removes it.
            final String typeName = ByteArraySerializer.get(true).deserialize(buffer);
            mtype = QMetaTypeRegistry.getType(typeName);

            if (mtype == null || mtype.serializer == null) {
                throw new IOException("Unknown type: " + typeName);
            }
        } else {
            mtype = QMetaTypeRegistry.getType(type);

            if (mtype == null || mtype.serializer == null) {
                throw new IOException("Unknown type: " + type.name());
            }
        }

        // Return data
        return new QVariant<>(mtype, mtype.serializer.deserialize(buffer));
    }
}
