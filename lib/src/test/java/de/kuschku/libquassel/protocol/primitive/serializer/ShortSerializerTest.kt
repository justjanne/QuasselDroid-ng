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
import kotlin.experimental.inv

class ShortSerializerTest {
  @Test
  fun testZero() {
    assertEquals(0.toShort(), roundTrip(ShortSerializer, 0.toShort()))
    // @formatter:off
    assertEquals(0.toShort(), deserialize(ShortSerializer, byteArrayOf(0, 0)))
    // @formatter:on
  }

  @Test
  fun testMinimal() {
    assertEquals(Short.MIN_VALUE, roundTrip(ShortSerializer, Short.MIN_VALUE))
    // @formatter:off
    assertEquals(Short.MIN_VALUE, deserialize(ShortSerializer, byteArrayOf(-128, 0)))
    // @formatter:on
  }

  @Test
  fun testMaximal() {
    assertEquals(Short.MAX_VALUE, roundTrip(ShortSerializer, Short.MAX_VALUE))
    // @formatter:off
    assertEquals(Short.MAX_VALUE, deserialize(ShortSerializer, byteArrayOf(127, -1)))
    // @formatter:on
  }

  @Test
  fun testAllOnes() {
    assertEquals((0.toShort().inv()), roundTrip(ShortSerializer, (0.toShort().inv())))
    // @formatter:off
    assertEquals((0.toShort().inv()), deserialize(ShortSerializer, byteArrayOf(-1, -1)))
    // @formatter:on
  }
}
