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
import de.kuschku.libquassel.protocol.testutil.matchers.BomMatcherChar
import de.kuschku.libquassel.protocol.testutil.testDeserialize
import de.kuschku.libquassel.protocol.testutil.testQtSerializerDirect
import de.kuschku.libquassel.protocol.testutil.testQtSerializerVariant
import org.junit.Test

class QCharSerializerTest {
  @Test
  fun testNull() {
    val value = '\u0000'
    testQtSerializerDirect(QCharSerializer, value)
    testQtSerializerVariant(QCharSerializer, value)
    // @formatter:off
    testDeserialize(QCharSerializer, value, byteBufferOf(0, 0))
    // @formatter:on
  }

  @Test
  fun testAllOnes() {
    val value = '\uFFFF'
    testQtSerializerDirect(QCharSerializer, value)
    testQtSerializerVariant(QCharSerializer, value)
    // @formatter:off
    testDeserialize(QCharSerializer, value, byteBufferOf(-1, -1))
    // @formatter:on
  }

  @Test
  fun testBOM1() {
    val value = '\uFFFE'
    testQtSerializerDirect(QCharSerializer, value)
    testQtSerializerVariant(QCharSerializer, value)
    // @formatter:off
    testDeserialize(QCharSerializer, BomMatcherChar(value), byteBufferOf(-2, -1))
    // @formatter:on
  }

  @Test
  fun testBOM2() {
    val value = '\uFEFF'
    testQtSerializerDirect(QCharSerializer, value)
    testQtSerializerVariant(QCharSerializer, value)
    // @formatter:off
    testDeserialize(QCharSerializer, BomMatcherChar(value), byteBufferOf(-1, -2))
    // @formatter:on
  }

  @Test
  fun testAlphabet() {
    for (index in 0..25) {
      val value = 'a' + index
      testQtSerializerDirect(QCharSerializer, value)
      testQtSerializerVariant(QCharSerializer, value)
      // @formatter:off
      testDeserialize(QCharSerializer, value, byteBufferOf(0, (97 + index).toByte()))
      // @formatter:on
    }
    for (index in 0..25) {
      val value = 'A' + index
      testQtSerializerDirect(QCharSerializer, value)
      testQtSerializerVariant(QCharSerializer, value)
      // @formatter:off
      testDeserialize(QCharSerializer, value, byteBufferOf(0, (65 + index).toByte()))
      // @formatter:on
    }
    for (index in 0..9) {
      val value = '0' + index
      testQtSerializerDirect(QCharSerializer, value)
      testQtSerializerVariant(QCharSerializer, value)
      // @formatter:off
      testDeserialize(QCharSerializer, value, byteBufferOf(0, (48 + index).toByte()))
      // @formatter:on
    }
  }

  @Test
  fun testAlphabetExtended() {
    for (value in listOf('ä', 'ö', 'ü', 'ß', 'æ', 'ø', 'µ')) {
      testQtSerializerDirect(QCharSerializer, value)
      testQtSerializerVariant(QCharSerializer, value)
    }
  }
}
