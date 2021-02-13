/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.justjanne.libquassel.protocol.serializers.primitive

import de.justjanne.libquassel.protocol.serializers.QtSerializers
import de.justjanne.libquassel.protocol.testutil.byteBufferOf
import de.justjanne.libquassel.protocol.testutil.matchers.TemporalMatcher
import de.justjanne.libquassel.protocol.testutil.qtSerializerTest
import de.justjanne.libquassel.protocol.variant.QtType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.chrono.JapaneseDate
import org.threeten.bp.temporal.Temporal

class DateTimeSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      DateTimeSerializer,
      QtSerializers.find<Temporal>(QtType.QDateTime),
    )
  }

  @Test
  fun testEpoch() = qtSerializerTest(
    DateTimeSerializer,
    Instant.EPOCH,
    byteBufferOf(0, 37, 61, -116, 0, 0, 0, 0, 2),
    matcher = ::TemporalMatcher
  )

  @Test
  fun testEpochAtTimezone() = qtSerializerTest(
    DateTimeSerializer,
    Instant.EPOCH.atOffset(ZoneOffset.ofTotalSeconds(1234)),
    byteBufferOf(0x00u, 0x25u, 0x3Du, 0x8Cu, 0x00u, 0x12u, 0xD4u, 0x50u, 0x03u, 0x00u, 0x00u, 0x04u, 0xD2u),
    matcher = ::TemporalMatcher
  )

  @Test
  fun testEpochByCalendarAtTimezone() = qtSerializerTest(
    DateTimeSerializer,
    LocalDateTime
      .of(1970, 1, 1, 0, 0)
      .atZone(ZoneId.of("Europe/Berlin"))
      .toInstant(),
    byteBufferOf(0, 37, 61, -117, 4, -17, 109, -128, 2),
    matcher = ::TemporalMatcher
  )

  @Test
  fun testNormalCase() = qtSerializerTest(
    DateTimeSerializer,
    LocalDateTime
      .of(2019, Month.JANUARY, 15, 20, 25)
      .atZone(ZoneId.of("Europe/Berlin"))
      .toInstant(),
    byteBufferOf(0, 37, -125, -125, 4, 42, -106, -32, 2),
    matcher = ::TemporalMatcher
  )

  @Test
  fun testLocalDateTime() = qtSerializerTest(
    DateTimeSerializer,
    LocalDateTime
      .of(2019, Month.JANUARY, 15, 20, 25),
    byteBufferOf(0x00u, 0x25u, 0x83u, 0x83u, 0x04u, 0x61u, 0x85u, 0x60u, 0xFFu),
    matcher = ::TemporalMatcher
  )

  @Test
  fun testZonedDateTime() = qtSerializerTest(
    DateTimeSerializer,
    LocalDateTime
      .of(2019, Month.JANUARY, 15, 20, 25)
      .atZone(ZoneId.systemDefault()),
    matcher = ::TemporalMatcher
  )

  @Test
  fun testUnknownDateTime() = qtSerializerTest(
    DateTimeSerializer,
    LocalDateTime
      .of(2019, Month.JANUARY, 15, 20, 25),
    byteBufferOf(0x00u, 0x25u, 0x83u, 0x83u, 0x04u, 0x61u, 0x85u, 0x60u, 0xFFu),
    matcher = ::TemporalMatcher
  )

  @Test
  fun testInvalidDateTime() = qtSerializerTest(
    DateTimeSerializer,
    LocalDateTime
      .of(2019, Month.JANUARY, 15, 20, 25),
    byteBufferOf(0x00u, 0x25u, 0x83u, 0x83u, 0x04u, 0x61u, 0x85u, 0x60u, 0x09u),
    matcher = ::TemporalMatcher,
    serializeFeatureSet = null,
    featureSets = emptyList(),
  )

  @Test
  fun testOldJavaDate() {
    assertThrows<IllegalArgumentException>("Unsupported Format: org.threeten.bp.chrono.JapaneseDate") {
      qtSerializerTest(
        DateTimeSerializer,
        JapaneseDate.now(),
        matcher = ::TemporalMatcher
      )
    }
  }
}
