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
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.nio.ByteBuffer

class ByteArraySerializerTest {
  @Test
  fun testBaseCase() {
    val value = byteArrayOf()
    assertArrayEquals(value, roundTrip(ByteArraySerializer, ByteBuffer.wrap(value))?.array())
    assertArrayEquals(value, deserialize(ByteArraySerializer, byteArrayOf(0, 0, 0, 0))?.array())
  }

  @Test
  fun testNormal() {
    val value = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    assertArrayEquals(value, roundTrip(ByteArraySerializer, ByteBuffer.wrap(value))?.array())
    assertArrayEquals(value, deserialize(ByteArraySerializer, byteArrayOf(0, 0, 0, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9))?.array())
  }
}
