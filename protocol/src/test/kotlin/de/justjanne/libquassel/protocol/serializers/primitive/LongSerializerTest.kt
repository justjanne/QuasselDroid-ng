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
 * with this program. If not,see <http://www.gnu.org/licenses/>.
 */
package de.justjanne.libquassel.protocol.serializers.primitive

import de.justjanne.libquassel.protocol.serializers.QtSerializers
import de.justjanne.libquassel.protocol.testutil.byteBufferOf
import de.justjanne.libquassel.protocol.testutil.qtSerializerTest
import de.justjanne.libquassel.protocol.variant.QtType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LongSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      LongSerializer,
      QtSerializers.find<Long>(QtType.Long),
    )
  }

  @Test
  fun testZero() = qtSerializerTest(
    LongSerializer,
    0L,
    byteBufferOf(0, 0, 0, 0, 0, 0, 0, 0)
  )

  @Test
  fun testMinimal() = qtSerializerTest(
    LongSerializer,
    Long.MIN_VALUE,
    byteBufferOf(-128, 0, 0, 0, 0, 0, 0, 0)
  )

  @Test
  fun testMaximal() = qtSerializerTest(
    LongSerializer,
    Long.MAX_VALUE,
    byteBufferOf(127, -1, -1, -1, -1, -1, -1, -1)
  )

  @Test
  fun testAllOnes() = qtSerializerTest(
    LongSerializer,
    0L.inv(),
    byteBufferOf(-1, -1, -1, -1, -1, -1, -1, -1)
  )
}
