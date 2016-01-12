package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.kuschku.libquassel.ClientData;
import de.kuschku.libquassel.primitives.types.Protocol;

public class ProtocolSerializer implements PrimitiveSerializer<Protocol> {
    private static final ProtocolSerializer serializer = new ProtocolSerializer();

    private ProtocolSerializer() {
    }

    public static ProtocolSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(ByteChannel channel, Protocol data) throws IOException {
        ByteSerializer.get().serialize(channel, data.protocolFlags.flags);
        ShortSerializer.get().serialize(channel, data.protocolData);
        ByteSerializer.get().serialize(channel, data.protocolVersion);
    }

    @Override
    public Protocol deserialize(ByteBuffer buffer) throws IOException {
        return new Protocol(
                new ClientData.FeatureFlags(ByteSerializer.get().deserialize(buffer)),
                ShortSerializer.get().deserialize(buffer),
                ByteSerializer.get().deserialize(buffer)
        );
    }
}
