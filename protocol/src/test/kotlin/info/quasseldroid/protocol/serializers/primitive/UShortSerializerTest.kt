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

class UShortSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      UShortSerializer,
      QtSerializers.find<UShort>(QtType.UShort),
    )
  }

  @Test
  fun testZero() = qtSerializerTest(
    UShortSerializer,
    0.toUShort(),
    byteBufferOf(0, 0)
  )

  @Test
  fun testMinimal() = qtSerializerTest(
    UShortSerializer,
    UShort.MIN_VALUE,
    byteBufferOf(0, 0)
  )

  @Test
  fun testMaximal() = qtSerializerTest(
    UShortSerializer,
    UShort.MAX_VALUE,
    byteBufferOf(255u, 255u)
  )

  @Test
  fun testAllOnes() = qtSerializerTest(
    UShortSerializer,
    0.toUShort().inv(),
    byteBufferOf(255u, 255u)
  )
}
