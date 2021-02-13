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
import org.threeten.bp.*
import org.threeten.bp.chrono.JapaneseDate
import org.threeten.bp.temporal.Temporal

class DateSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      DateSerializer,
      QtSerializers.find<LocalDate>(QtType.QDate),
    )
  }

  @Test
  fun testEpoch() = qtSerializerTest(
    DateSerializer,
    LocalDate
      .of(1970, 1, 1),
    byteBufferOf(0, 37, 61, -116),
    matcher = ::TemporalMatcher
  )

  @Test
  fun testNormalCase() = qtSerializerTest(
    DateSerializer,
    LocalDate
      .of(2019, Month.JANUARY, 15),
    byteBufferOf(0, 37, -125, -125),
    matcher = ::TemporalMatcher
  )
}
