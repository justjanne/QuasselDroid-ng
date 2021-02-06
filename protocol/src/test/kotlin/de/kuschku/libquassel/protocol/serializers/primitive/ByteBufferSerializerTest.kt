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
import de.kuschku.libquassel.protocol.testutil.matchers.ByteBufferMatcher
import de.kuschku.libquassel.protocol.testutil.testDeserialize
import de.kuschku.libquassel.protocol.testutil.testQtSerializerDirect
import org.junit.Test

class ByteBufferSerializerTest {
  @Test
  fun testBaseCase() {
    val value = byteBufferOf(0)
    testQtSerializerDirect(ByteBufferSerializer, value, ByteBufferMatcher(value))
    // @formatter:off
    testDeserialize(ByteBufferSerializer, ByteBufferMatcher(value), byteBufferOf(0, 0, 0, 1, 0))
    // @formatter:on
  }

  @Test
  fun testNormal() {
    val value = byteBufferOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    testQtSerializerDirect(ByteBufferSerializer, value, ByteBufferMatcher(value))
    // @formatter:off
    testDeserialize(ByteBufferSerializer, ByteBufferMatcher(value), byteBufferOf(0, 0, 0, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9))
    // @formatter:on
  }
}
