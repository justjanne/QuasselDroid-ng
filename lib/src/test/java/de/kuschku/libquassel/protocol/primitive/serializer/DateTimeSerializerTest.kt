/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.deserialize
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import org.threeten.bp.ZoneOffset

class DateTimeSerializerTest {
  @Test
  fun testEpoch() {
    val value = roundTrip(DateTimeSerializer, Instant.EPOCH)
    assertEquals(Instant.EPOCH, value)
    assertEquals(Instant.EPOCH, deserialize(DateTimeSerializer, byteArrayOf(0, 37, 61, -116, 0, 0, 0, 0, 2)))
  }

  @Test
  fun testEpochAtTimezone() {
    val value = Instant.EPOCH.atOffset(ZoneOffset.ofTotalSeconds(1234))
    assertEquals(value, roundTrip(DateTimeSerializer, value))
    assertEquals(value, deserialize(DateTimeSerializer, byteArrayOf(0, 37, 61, -116, 0, 18, -44, 80, 3, 0, 0, 4, -46)))
  }

  @Test
  fun testEpochByCalendarAtTimezone() {
    val value = LocalDateTime.of(1970, 1, 1, 0, 0)
      .atZone(ZoneOffset.systemDefault()).toInstant()
    assertEquals(value, roundTrip(DateTimeSerializer, value))
    assertEquals(value, deserialize(DateTimeSerializer, byteArrayOf(0, 37, 61, -117, 4, -17, 109, -128, 2)))
  }

  @Test
  fun testNormalCase() {
    val value = LocalDateTime.of(2019, Month.JANUARY, 15, 20, 25)
      .atZone(ZoneOffset.systemDefault()).toInstant()
    assertEquals(value, roundTrip(DateTimeSerializer, value))
    assertEquals(value, deserialize(DateTimeSerializer, byteArrayOf(0, 37, -125, -125, 4, 42, -106, -32, 2)))
  }
}
