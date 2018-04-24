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
