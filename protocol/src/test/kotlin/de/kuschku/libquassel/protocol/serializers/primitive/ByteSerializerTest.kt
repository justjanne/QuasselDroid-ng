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
import kotlin.experimental.inv

class ByteSerializerTest {
  @Test
  fun testZero() {
    val value = 0.toByte()
    testQtSerializerDirect(ByteSerializer, value)
    testQtSerializerVariant(ByteSerializer, value)
    // @formatter:off
    testDeserialize(ByteSerializer, value, byteBufferOf(0))
    // @formatter:on
  }

  @Test
  fun testMinimal() {
    val value = Byte.MIN_VALUE
    testQtSerializerDirect(ByteSerializer, value)
    testQtSerializerVariant(ByteSerializer, value)
    // @formatter:off
    testDeserialize(ByteSerializer, value, byteBufferOf(-128))
    // @formatter:on
  }

  @Test
  fun testMaximal() {
    val value = Byte.MAX_VALUE
    testQtSerializerDirect(ByteSerializer, value)
    testQtSerializerVariant(ByteSerializer, value)
    // @formatter:off
    testDeserialize(ByteSerializer, value, byteBufferOf(127))
    // @formatter:on
  }

  @Test
  fun testAllOnes() {
    val value = 0.toByte().inv()

    testQtSerializerDirect(ByteSerializer, value)
    testQtSerializerVariant(ByteSerializer, value)
    // @formatter:off
    testDeserialize(ByteSerializer, value, byteBufferOf(-1))
    // @formatter:on
  }
}
