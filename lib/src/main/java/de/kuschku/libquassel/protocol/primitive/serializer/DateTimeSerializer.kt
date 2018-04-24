/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import org.threeten.bp.*
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.JulianFields
import org.threeten.bp.temporal.Temporal
import java.nio.ByteBuffer

object DateTimeSerializer : Serializer<Temporal> {
  enum class TimeSpec(val value: Byte) {
    LocalTime(0),
    UTC(1),
    OffsetFromUTC(2),
    TimeZone(3);

    companion object {
      private val map = TimeSpec.values().associateBy(TimeSpec::value)
      fun of(type: Byte) = map[type]
    }
  }

  override fun serialize(buffer: ChainedByteBuffer, data: Temporal, features: QuasselFeatures) {
    when (data) {
      is LocalDateTime  -> {
        IntSerializer.serialize(buffer, data.getLong(JulianFields.JULIAN_DAY).toInt(), features)
        IntSerializer.serialize(buffer, data.getLong(ChronoField.MILLI_OF_DAY).toInt(), features)
        ByteSerializer.serialize(buffer, TimeSpec.LocalTime.value, features)
      }
      is OffsetDateTime -> {
        IntSerializer.serialize(buffer, data.getLong(JulianFields.JULIAN_DAY).toInt(), features)
        IntSerializer.serialize(buffer, data.getLong(ChronoField.MILLI_OF_DAY).toInt(), features)
        ByteSerializer.serialize(buffer, TimeSpec.OffsetFromUTC.value, features)
        IntSerializer.serialize(buffer, data.offset.totalSeconds, features)
      }
      is ZonedDateTime  -> {
        IntSerializer.serialize(buffer, data.getLong(JulianFields.JULIAN_DAY).toInt(), features)
        IntSerializer.serialize(buffer, data.getLong(ChronoField.MILLI_OF_DAY).toInt(), features)
        ByteSerializer.serialize(buffer, TimeSpec.OffsetFromUTC.value, features)
        IntSerializer.serialize(buffer, data.offset.totalSeconds, features)
      }
      is Instant        -> {
        val time = data.atOffset(ZoneOffset.UTC)
        IntSerializer.serialize(buffer, time.getLong(JulianFields.JULIAN_DAY).toInt(), features)
        IntSerializer.serialize(buffer, time.getLong(ChronoField.MILLI_OF_DAY).toInt(), features)
        ByteSerializer.serialize(buffer, TimeSpec.UTC.value, features)
      }
      else              ->
        throw IllegalArgumentException(
          "Unsupported Format: ${data::class.java.canonicalName}"
        )
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): Temporal {
    val julianDay = IntSerializer.deserialize(buffer, features).toLong()
    val milliOfDay = IntSerializer.deserialize(buffer, features).toLong()
    val timeSpec = TimeSpec.of(ByteSerializer.deserialize(buffer, features))
    if (milliOfDay == -1L || julianDay == -1L)
      return Instant.EPOCH
    return when (timeSpec) {
      TimeSpec.LocalTime ->
        Instant.EPOCH.atZone(ZoneOffset.systemDefault())
          .with(JulianFields.JULIAN_DAY, julianDay)
          .with(ChronoField.MILLI_OF_DAY, milliOfDay)
          .toInstant()
      else               ->
        Instant.EPOCH.atOffset(ZoneOffset.UTC)
          .with(JulianFields.JULIAN_DAY, julianDay)
          .with(ChronoField.MILLI_OF_DAY, milliOfDay)
          .toInstant()
    }
  }
}
