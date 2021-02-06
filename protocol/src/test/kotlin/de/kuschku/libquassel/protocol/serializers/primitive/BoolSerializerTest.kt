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

class BoolSerializerTest {
  @Test
  fun testTrue() {
    testQtSerializerDirect(BoolSerializer, true)
    testQtSerializerVariant(BoolSerializer, true)
    // @formatter:off
    testDeserialize(BoolSerializer, true, byteBufferOf(1))
    // @formatter:on
  }

  @Test
  fun testFalse() {
    testQtSerializerDirect(BoolSerializer, false)
    testQtSerializerVariant(BoolSerializer, false)
    // @formatter:off
    testDeserialize(BoolSerializer, false, byteBufferOf(0))
    // @formatter:on
  }
}
