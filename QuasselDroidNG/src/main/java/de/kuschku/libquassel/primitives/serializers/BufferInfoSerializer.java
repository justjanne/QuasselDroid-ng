package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.kuschku.libquassel.primitives.types.BufferInfo;

public class BufferInfoSerializer implements PrimitiveSerializer<BufferInfo> {
    @NonNull
    private static final BufferInfoSerializer serializer = new BufferInfoSerializer();
    private BufferInfoSerializer() {}
    @NonNull
    public static BufferInfoSerializer get(){
        return serializer;
    }

    @Override
    public void serialize(@NonNull ByteChannel channel, @NonNull BufferInfo data) throws IOException {
        IntSerializer.get().serialize(channel, data.id);
        IntSerializer.get().serialize(channel, data.networkId);
        ShortSerializer.get().serialize(channel, data.type.id);
        IntSerializer.get().serialize(channel, data.groupId);
        ByteArraySerializer.get().serialize(channel, data.name);
    }

    @NonNull
    @Override
    public BufferInfo deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        return new BufferInfo(
                IntSerializer.get().deserialize(buffer),
                IntSerializer.get().deserialize(buffer),
                BufferInfo.Type.fromId(ShortSerializer.get().deserialize(buffer)),
                IntSerializer.get().deserialize(buffer),
                ByteArraySerializer.get().deserialize(buffer)
        );
    }
}
