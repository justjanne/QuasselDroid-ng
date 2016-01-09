package de.kuschku.libquassel.primitives.serializers;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class DateTimeSerializer implements PrimitiveSerializer<DateTime> {
    @Override
    public void serialize(final ByteChannel channel, final DateTime data) throws IOException {
        final boolean isUTC;
        final DateTimeZone zone = data.getZone();
        if (zone.equals(DateTimeZone.UTC)) isUTC = true;
        else if (zone.equals(DateTimeZone.getDefault())) isUTC = false;
            // TODO: Add serialization for other timezones
        else
            throw new IllegalArgumentException("Serialization of timezones except for local and UTC is not supported");


        new IntSerializer().serialize(channel, (int) DateTimeUtils.toJulianDayNumber(data.getMillis()));
        new IntSerializer().serialize(channel, data.getMillisOfDay());
        new BoolSerializer().serialize(channel, isUTC);
    }

    @Override
    public DateTime deserialize(final ByteBuffer buffer) throws IOException {
        final long julianDay = new IntSerializer().deserialize(buffer);
        final int millisSinceMidnight = new IntSerializer().deserialize(buffer);

        final short zone = new ByteSerializer().deserialize(buffer);

        if (millisSinceMidnight == 0x73007300 && julianDay == 0x50006100 || millisSinceMidnight == -1 || julianDay == -1)
            return new DateTime(0);

        if ((zone & 0xfffffff0) > 0) {
            throw new IllegalArgumentException("Deserialization of timezones except for local and UTC is not supported: " + zone);
        }

        DateTime time = new DateTime(DateTimeUtils.fromJulianDay(julianDay));
        time = time.millisOfDay().setCopy(millisSinceMidnight);
        if (zone == 0) time = time.withZoneRetainFields(DateTimeZone.getDefault());
        else time = time.withZoneRetainFields(DateTimeZone.UTC);

        return time;
    }
}
