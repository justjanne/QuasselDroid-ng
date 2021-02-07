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

class UShortSerializerTest {
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