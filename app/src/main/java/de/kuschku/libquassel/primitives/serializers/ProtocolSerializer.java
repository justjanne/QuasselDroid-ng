package de.kuschku.libquassel.primitives.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.kuschku.libquassel.ClientData;
import de.kuschku.libquassel.primitives.types.Protocol;

public class ProtocolSerializer implements PrimitiveSerializer<Protocol> {
    @Override
    public void serialize(ByteChannel channel, Protocol data) throws IOException {
        new ByteSerializer().serialize(channel, data.protocolFlags.flags);
        new ShortSerializer().serialize(channel, data.protocolData);
        new ByteSerializer().serialize(channel, data.protocolVersion);
    }

    @Override
    public Protocol deserialize(ByteBuffer buffer) throws IOException {
        return new Protocol(
                new ClientData.FeatureFlags(new ByteSerializer().deserialize(buffer)),
                new ShortSerializer().deserialize(buffer),
                new ByteSerializer().deserialize(buffer)
        );
    }
}
