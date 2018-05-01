/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
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
import kotlin.experimental.inv

class ByteSerializerTest {
  @Test
  fun testZero() {
    assertEquals(0.toByte(), roundTrip(ByteSerializer, 0.toByte()))
  }

  @Test
  fun testMinimal() {
    assertEquals(Byte.MIN_VALUE, roundTrip(ByteSerializer, Byte.MIN_VALUE))
  }

  @Test
  fun testMaximal() {
    assertEquals(Byte.MAX_VALUE, roundTrip(ByteSerializer, Byte.MAX_VALUE))
  }

  @Test
  fun testAllOnes() {
    assertEquals((0.toByte().inv()), roundTrip(ByteSerializer, (0.toByte().inv())))
  }
}
