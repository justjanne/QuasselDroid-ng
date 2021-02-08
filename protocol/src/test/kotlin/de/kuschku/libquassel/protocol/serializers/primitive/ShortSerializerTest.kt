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
package de.kuschku.libquassel.protocol.serializers.primitive

import de.kuschku.libquassel.protocol.serializers.QtSerializers
import de.kuschku.libquassel.protocol.testutil.byteBufferOf
import de.kuschku.libquassel.protocol.testutil.qtSerializerTest
import de.kuschku.libquassel.protocol.variant.QtType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.experimental.inv

class ShortSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      ShortSerializer,
      QtSerializers.find<Short>(QtType.Short),
    )
  }

  @Test
  fun testZero() = qtSerializerTest(
    ShortSerializer,
    0.toShort(),
    byteBufferOf(0, 0)
  )

  @Test
  fun testMinimal() = qtSerializerTest(
    ShortSerializer,
    Short.MIN_VALUE,
    byteBufferOf(-128, 0)
  )

  @Test
  fun testMaximal() = qtSerializerTest(
    ShortSerializer,
    Short.MAX_VALUE,
    byteBufferOf(127, -1)
  )

  @Test
  fun testAllOnes() = qtSerializerTest(
    ShortSerializer,
    0.toShort().inv(),
    byteBufferOf(-1, -1)
  )
}
