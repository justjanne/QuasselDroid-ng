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

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.deserialize
import de.kuschku.libquassel.util.roundTrip
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IntSerializerTest {
  @Test
  fun testZero() {
    assertEquals(0, roundTrip(IntSerializer, 0))
    // @formatter:off
    assertEquals(0, deserialize(IntSerializer, byteArrayOf(0, 0, 0, 0)))
    // @formatter:on
  }

  @Test
  fun testMinimal() {
    assertEquals(Int.MIN_VALUE, roundTrip(IntSerializer, Int.MIN_VALUE))
    // @formatter:off
    assertEquals(Int.MIN_VALUE, deserialize(IntSerializer, byteArrayOf(-128, 0, 0, 0)))
    // @formatter:on
  }

  @Test
  fun testMaximal() {
    assertEquals(Int.MAX_VALUE, roundTrip(IntSerializer, Int.MAX_VALUE))
    // @formatter:off
    assertEquals(Int.MAX_VALUE, deserialize(IntSerializer, byteArrayOf(127, -1, -1, -1)))
    // @formatter:on
  }

  @Test
  fun testAllOnes() {
    assertEquals(0.inv(), roundTrip(IntSerializer, 0.inv()))
    // @formatter:off
    assertEquals(0.inv(), deserialize(IntSerializer, byteArrayOf(-1, -1, -1, -1)))
    // @formatter:on
  }
}
