package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class DateTimeSerializerTest {
  @Test
  fun testEpoch() {
    val value = roundTrip(DateTimeSerializer, Instant.EPOCH)
    assertEquals(Instant.EPOCH, value)
  }

  @Test
  fun testEpochAtTimezone() {
    val value = Instant.EPOCH.atOffset(ZoneOffset.ofTotalSeconds(1234))
    assertEquals(
      value.atZoneSimilarLocal(ZoneOffset.UTC).toInstant(),
      roundTrip(DateTimeSerializer, value)
    )
  }

  @Test
  fun testEpochByCalendarAtTimezone() {
    val value = LocalDateTime.of(1970, 1, 1, 0, 0)
      .atZone(ZoneOffset.systemDefault()).toInstant()
    assertEquals(value, roundTrip(DateTimeSerializer, value))
  }

  @Test
  fun testNormalCase() {
    val value = LocalDateTime.now().atZone(ZoneOffset.systemDefault()).toInstant()
    assertEquals(value, roundTrip(DateTimeSerializer, value))
  }
}
