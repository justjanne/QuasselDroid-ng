package de.kuschku.libquassel.primitives.serializers;

import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class TimeSerializer implements PrimitiveSerializer<DateTime> {
    @Override
    public void serialize(final ByteChannel channel, final DateTime data) throws IOException {
        new IntSerializer().serialize(channel, data.millisOfDay().get());
    }

    @Override
    public DateTime deserialize(final ByteBuffer buffer) throws IOException {
        return DateTime.now().millisOfDay().setCopy(new IntSerializer().deserialize(buffer));
    }
}
