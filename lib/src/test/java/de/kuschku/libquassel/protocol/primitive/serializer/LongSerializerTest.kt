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

class LongSerializerTest {
  @Test
  fun testZero() {
    assertEquals(0L, roundTrip(LongSerializer, 0L))
    // @formatter:off
    assertEquals(0L, deserialize(LongSerializer, byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)))
    // @formatter:on
  }

  @Test
  fun testMinimal() {
    assertEquals(Long.MIN_VALUE, roundTrip(LongSerializer, Long.MIN_VALUE))
    // @formatter:off
    assertEquals(Long.MIN_VALUE, deserialize(LongSerializer, byteArrayOf(-128, 0, 0, 0, 0, 0, 0, 0)))
    // @formatter:on
  }

  @Test
  fun testMaximal() {
    assertEquals(Long.MAX_VALUE, roundTrip(LongSerializer, Long.MAX_VALUE))
    // @formatter:off
    assertEquals(Long.MAX_VALUE, deserialize(LongSerializer, byteArrayOf(127, -1, -1, -1, -1, -1, -1, -1)))
    // @formatter:on
  }

  @Test
  fun testAllOnes() {
    assertEquals(0L.inv(), roundTrip(LongSerializer, 0L.inv()))
    // @formatter:off
    assertEquals(0L.inv(), deserialize(LongSerializer, byteArrayOf(-1, -1, -1, -1, -1, -1, -1, -1)))
    // @formatter:on
  }
}
