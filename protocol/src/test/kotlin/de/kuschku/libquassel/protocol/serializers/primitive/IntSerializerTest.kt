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
import de.kuschku.libquassel.protocol.testutil.testDeserialize
import de.kuschku.libquassel.protocol.testutil.testQtSerializerDirect
import de.kuschku.libquassel.protocol.testutil.testQtSerializerVariant
import org.junit.Test

class IntSerializerTest {
  @Test
  fun testZero() {
    val value = 0
    testQtSerializerDirect(IntSerializer, value)
    testQtSerializerVariant(IntSerializer, value)
    // @formatter:off
    testDeserialize(IntSerializer, value, byteBufferOf(0, 0, 0, 0))
    // @formatter:on
  }

  @Test
  fun testMinimal() {
    val value = Int.MIN_VALUE
    testQtSerializerDirect(IntSerializer, value)
    testQtSerializerVariant(IntSerializer, value)
    // @formatter:off
    testDeserialize(IntSerializer, value, byteBufferOf(-128, 0, 0, 0))
    // @formatter:on
  }

  @Test
  fun testMaximal() {
    val value = Int.MAX_VALUE
    testQtSerializerDirect(IntSerializer, value)
    testQtSerializerVariant(IntSerializer, value)
    // @formatter:off
    testDeserialize(IntSerializer, value, byteBufferOf(127, -1, -1, -1))
    // @formatter:on
  }

  @Test
  fun testAllOnes() {
    val value = 0.inv()

    testQtSerializerDirect(IntSerializer, value)
    testQtSerializerVariant(IntSerializer, value)
    // @formatter:off
    testDeserialize(IntSerializer, value, byteBufferOf(-1, -1, -1, -1))
    // @formatter:on
  }
}
