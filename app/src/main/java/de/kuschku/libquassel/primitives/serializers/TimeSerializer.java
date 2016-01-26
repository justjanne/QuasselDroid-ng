package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class TimeSerializer implements PrimitiveSerializer<DateTime> {
    @NonNull
    private static final TimeSerializer serializer = new TimeSerializer();

    private TimeSerializer() {
    }

    @NonNull
    public static TimeSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final DateTime data) throws IOException {
        IntSerializer.get().serialize(channel, data.millisOfDay().get());
    }

    @NonNull
    @Override
    public DateTime deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        return DateTime.now().millisOfDay().setCopy(IntSerializer.get().deserialize(buffer));
    }
}
