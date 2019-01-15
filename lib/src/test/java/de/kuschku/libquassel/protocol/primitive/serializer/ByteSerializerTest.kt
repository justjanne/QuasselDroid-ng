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

class ByteSerializerTest {
  @Test
  fun testZero() {
    assertEquals(0.toByte(), roundTrip(ByteSerializer, 0.toByte()))
    assertEquals(0.toByte(), deserialize(ByteSerializer, byteArrayOf(0)))
  }

  @Test
  fun testMinimal() {
    assertEquals(Byte.MIN_VALUE, roundTrip(ByteSerializer, Byte.MIN_VALUE))
    assertEquals(Byte.MIN_VALUE, deserialize(ByteSerializer, byteArrayOf(-128)))
  }

  @Test
  fun testMaximal() {
    assertEquals(Byte.MAX_VALUE, roundTrip(ByteSerializer, Byte.MAX_VALUE))
    assertEquals(Byte.MAX_VALUE, deserialize(ByteSerializer, byteArrayOf(127)))
  }

  @Test
  fun testAllOnes() {
    assertEquals((0.toByte().inv()), roundTrip(ByteSerializer, (0.toByte().inv())))
    assertEquals((0.toByte().inv()), deserialize(ByteSerializer, byteArrayOf(-1)))
  }
}
