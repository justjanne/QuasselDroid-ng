package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
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

  override fun serialize(buffer: ChainedByteBuffer, data: Temporal, features: Quassel_Features) {
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
      is Instant        -> {
        val time = data.atOffset(ZoneOffset.UTC)
        IntSerializer.serialize(buffer, time.getLong(JulianFields.JULIAN_DAY).toInt(), features)
        IntSerializer.serialize(buffer, time.getLong(ChronoField.MILLI_OF_DAY).toInt(), features)
        ByteSerializer.serialize(buffer, TimeSpec.UTC.value, features)
      }
      else              ->
        throw IllegalArgumentException(
          "Unsupported Format: ${data::class.java.canonicalName}")
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): Temporal {
    val julianDay = IntSerializer.deserialize(buffer, features).toLong()
    val milliOfDay = IntSerializer.deserialize(buffer, features).toLong()
    val timeSpec = TimeSpec.of(ByteSerializer.deserialize(buffer, features))
    if (milliOfDay == -1L || julianDay == -1L)
      return Instant.EPOCH
    return when (timeSpec) {
      TimeSpec.LocalTime ->
        LocalDateTime.now()
          .with(JulianFields.JULIAN_DAY, julianDay)
          .with(ChronoField.MILLI_OF_DAY, milliOfDay)
          .atZone(ZoneOffset.systemDefault())
          .toInstant()
      else               ->
        OffsetDateTime.now()
          .with(JulianFields.JULIAN_DAY, julianDay)
          .with(ChronoField.MILLI_OF_DAY, milliOfDay)
          .withOffsetSameLocal(ZoneOffset.UTC)
          .toInstant()
    }
  }
}
