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
package info.quasseldroid.protocol.serializers.primitive

import info.quasseldroid.protocol.serializers.QtSerializers
import info.quasseldroid.protocol.testutil.byteBufferOf
import info.quasseldroid.protocol.testutil.qtSerializerTest
import info.quasseldroid.protocol.variant.QtType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IntSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      IntSerializer,
      QtSerializers.find<Int>(QtType.Int),
    )
  }

  @Test
  fun testZero() = qtSerializerTest(
    IntSerializer,
    0,
    byteBufferOf(0, 0, 0, 0)
  )

  @Test
  fun testMinimal() = qtSerializerTest(
    IntSerializer,
    Int.MIN_VALUE,
    byteBufferOf(-128, 0, 0, 0)
  )

  @Test
  fun testMaximal() = qtSerializerTest(
    IntSerializer,
    Int.MAX_VALUE,
    byteBufferOf(127, -1, -1, -1)
  )

  @Test
  fun testAllOnes() = qtSerializerTest(
    IntSerializer,
    0.inv(),
    byteBufferOf(-1, -1, -1, -1)
  )
}
