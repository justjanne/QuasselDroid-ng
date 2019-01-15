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
import kotlin.experimental.inv

class ShortSerializerTest {
  @Test
  fun testZero() {
    assertEquals(0.toShort(), roundTrip(ShortSerializer, 0.toShort()))
    assertEquals(0.toShort(), deserialize(ShortSerializer, byteArrayOf(0, 0)))
  }

  @Test
  fun testMinimal() {
    assertEquals(Short.MIN_VALUE, roundTrip(ShortSerializer, Short.MIN_VALUE))
    assertEquals(Short.MIN_VALUE, deserialize(ShortSerializer, byteArrayOf(-128, 0)))
  }

  @Test
  fun testMaximal() {
    assertEquals(Short.MAX_VALUE, roundTrip(ShortSerializer, Short.MAX_VALUE))
    assertEquals(Short.MAX_VALUE, deserialize(ShortSerializer, byteArrayOf(127, -1)))
  }

  @Test
  fun testAllOnes() {
    assertEquals((0.toShort().inv()), roundTrip(ShortSerializer, (0.toShort().inv())))
    assertEquals((0.toShort().inv()), deserialize(ShortSerializer, byteArrayOf(-1, -1)))
  }
}
