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

import info.quasseldroid.protocol.serializers.QuasselSerializers
import info.quasseldroid.protocol.testutil.byteBufferOf
import info.quasseldroid.protocol.testutil.quasselSerializerTest
import info.quasseldroid.protocol.types.BufferId
import info.quasseldroid.protocol.variant.QuasselType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BufferIdSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      BufferIdSerializer,
      QuasselSerializers.find<BufferId>(QuasselType.BufferId),
    )
  }

  @Test
  fun testZero() = quasselSerializerTest(
    BufferIdSerializer,
    BufferId(0),
    byteBufferOf(0, 0, 0, 0)
  )

  @Test
  fun testMinimal() = quasselSerializerTest(
    BufferIdSerializer,
    BufferId.MIN_VALUE,
    byteBufferOf(-128, 0, 0, 0)
  )

  @Test
  fun testMaximal() = quasselSerializerTest(
    BufferIdSerializer,
    BufferId.MAX_VALUE,
    byteBufferOf(127, -1, -1, -1)
  )

  @Test
  fun testAllOnes() = quasselSerializerTest(
    BufferIdSerializer,
    BufferId(0.inv()),
    byteBufferOf(-1, -1, -1, -1)
  )
}
