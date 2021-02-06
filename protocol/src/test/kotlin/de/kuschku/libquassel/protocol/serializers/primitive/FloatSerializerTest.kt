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

import de.kuschku.libquassel.protocol.testutil.byteBufferOf
import de.kuschku.libquassel.protocol.testutil.qtSerializerTest
import org.junit.jupiter.api.Test

class FloatSerializerTest {
  @Test
  fun testZero() = qtSerializerTest(
    FloatSerializer,
    0f,
    byteBufferOf(0x00u, 0x00u, 0x00u, 0x00u)
  )

  @Test
  fun testMinimal() = qtSerializerTest(
    FloatSerializer,
    Float.MIN_VALUE,
    byteBufferOf(0x00u, 0x00u, 0x00u, 0x01u)
  )

  @Test
  fun testMaximal() = qtSerializerTest(
    FloatSerializer,
    Float.MAX_VALUE,
    byteBufferOf(0x7Fu, 0x7Fu, 0xFFu, 0xFFu)
  )

  @Test
  fun testInfinityPositive() = qtSerializerTest(
    FloatSerializer,
    Float.POSITIVE_INFINITY,
    byteBufferOf(0x7Fu, 0x80u, 0x00u, 0x00u)
  )

  @Test
  fun testInfinityNegative() = qtSerializerTest(
    FloatSerializer,
    Float.NEGATIVE_INFINITY,
    byteBufferOf(0xFFu, 0x80u, 0x00u, 0x00u)
  )

  @Test
  fun testNotANumber() = qtSerializerTest(
    FloatSerializer,
    Float.NaN,
    byteBufferOf(0x7Fu, 0xC0u, 0x00u, 0x00u)
  )
}
