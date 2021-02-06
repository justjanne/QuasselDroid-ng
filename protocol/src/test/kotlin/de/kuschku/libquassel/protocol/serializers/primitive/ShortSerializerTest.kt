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

class ShortSerializerTest {
  @Test
  fun testZero() {
    val value = 0.toShort()
    testQtSerializerDirect(ShortSerializer, value)
    testQtSerializerVariant(ShortSerializer, value)
    // @formatter:off
    testDeserialize(ShortSerializer, value, byteBufferOf(0, 0))
    // @formatter:on
  }

  @Test
  fun testMinimal() {
    val value = Short.MIN_VALUE
    testQtSerializerDirect(ShortSerializer, value)
    testQtSerializerVariant(ShortSerializer, value)
    // @formatter:off
    testDeserialize(ShortSerializer, value, byteBufferOf(-128, 0))
    // @formatter:on
  }

  @Test
  fun testMaximal() {
    val value = Short.MAX_VALUE
    testQtSerializerDirect(ShortSerializer, value)
    testQtSerializerVariant(ShortSerializer, value)
    // @formatter:off
    testDeserialize(ShortSerializer, value, byteBufferOf(127, -1))
    // @formatter:on
  }

  @Test
  fun testAllOnes() {
    val value = 0.toShort().inv()

    testQtSerializerDirect(ShortSerializer, value)
    testQtSerializerVariant(ShortSerializer, value)
    // @formatter:off
    testDeserialize(ShortSerializer, value, byteBufferOf(-1, -1))
    // @formatter:on
  }
}
