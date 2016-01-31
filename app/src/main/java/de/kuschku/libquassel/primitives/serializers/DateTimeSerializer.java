/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import de.kuschku.util.Objects;

public class DateTimeSerializer implements PrimitiveSerializer<DateTime> {
    @NonNull
    private static final DateTimeSerializer serializer = new DateTimeSerializer();

    private DateTimeSerializer() {
    }

    @NonNull
    public static DateTimeSerializer get() {
        return serializer;
    }

    @Override
    public void serialize(@NonNull final ByteChannel channel, @NonNull final DateTime data) throws IOException {
        final boolean isUTC;
        final DateTimeZone zone = data.getZone();
        if (Objects.equals(zone, DateTimeZone.UTC)) isUTC = true;
        else if (Objects.equals(zone, DateTimeZone.getDefault())) isUTC = false;
            // TODO: Add serialization for other timezones
        else
            throw new IllegalArgumentException("Serialization of timezones except for local and UTC is not supported");


        IntSerializer.get().serialize(channel, (int) DateTimeUtils.toJulianDayNumber(data.getMillis()));
        IntSerializer.get().serialize(channel, data.getMillisOfDay());
        BoolSerializer.get().serialize(channel, isUTC);
    }

    @NonNull
    @Override
    public DateTime deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final long julianDay = IntSerializer.get().deserialize(buffer);
        final int millisSinceMidnight = IntSerializer.get().deserialize(buffer);

        final short zone = ByteSerializer.get().deserialize(buffer);

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
