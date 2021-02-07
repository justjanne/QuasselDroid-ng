/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package de.kuschku.libquassel.protocol.io

import de.kuschku.libquassel.protocol.testutil.matchers.ByteBufferMatcher
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.AssertionError
import java.nio.ByteBuffer

class ChainedByteBufferTest {

  @Test
  fun testPutArray() {
    validateArray(byteArrayOf())
    validateArray(byteArrayOf(0x00))
    validateArray(byteArrayOf(0x01))
    validateArray(byteArrayOf(0xFF.toByte()))
    validateArray(byteArrayOf(
      0x00, 0x01, -0x01, 0x00, 0x00, 0x01, -0x01, 0x00,
      0x00, 0x01, -0x01, 0x00, 0x00, 0x01, -0x01, 0x00,
      0x00, 0x01, -0x01, 0x00, 0x00, 0x01, -0x01, 0x00,
      0x00, 0x01, -0x01, 0x00, 0x00, 0x01, -0x01, 0x00,
      0x00, 0x01, -0x01, 0x00, 0x00, 0x01, -0x01, 0x00,
      0x00, 0x01, -0x01, 0x00, 0x00, 0x01, -0x01, 0x00,
      0x00, 0x01, -0x01, 0x00, 0x00, 0x01, -0x01, 0x00,
      0x00, 0x01, -0x01, 0x00, 0x00, 0x01, -0x01, 0x00,
    ))
    validateArray(ByteArray(3000, Int::toByte))
  }

  @Test
  fun testLimit() {
    assertThrows<IllegalArgumentException>(
      "Can not allocate 10 bytes, currently at 50, limit is 50"
    ) {
      ChainedByteBuffer(chunkSize = 10, limit = 50)
        .put(ByteArray(70, Int::toByte))
    }
    assertDoesNotThrow {
      ChainedByteBuffer(chunkSize = 10, limit = 70)
        .put(ByteArray(70, Int::toByte))
    }
    assertDoesNotThrow {
      ChainedByteBuffer(chunkSize = 10, limit = 70)
        .put(ByteArray(50, Int::toByte))
    }
    assertDoesNotThrow {
      ChainedByteBuffer(chunkSize = 10, limit = -1)
        .put(ByteArray(50, Int::toByte))
    }
    assertDoesNotThrow {
      ChainedByteBuffer(chunkSize = 10)
        .put(ByteArray(50, Int::toByte))
    }
  }

  @Test
  fun testClear() {
    val chained = ChainedByteBuffer(limit = 16384)
    val array = ByteArray(3000, Int::toByte)
    chained.put(array)
    assertEquals(array.size, chained.size)
    assertEquals(array.size, chained.toBuffer().remaining())
    assertThat(chained.toBuffer(), ByteBufferMatcher(ByteBuffer.wrap(array)))
    chained.clear()
    assertEquals(0, chained.size)
    assertEquals(0, chained.toBuffer().remaining())
    assertThat(chained.toBuffer(), ByteBufferMatcher(ByteBuffer.allocate(0)))
  }

  private fun validateArray(array: ByteArray) {
    fun validateArrayInternal(array: ByteArray, direct: Boolean) {
      val bufferSize = 1024
      val chained = ChainedByteBuffer(chunkSize = bufferSize, direct = direct, limit = 16384)
      chained.put(array)
      assertEquals(array.size, chained.size)
      assertEquals(array.size, chained.toBuffer().remaining())
      assertThat(chained.toBuffer(), ByteBufferMatcher(ByteBuffer.wrap(array)))
      if (array.size < bufferSize && array.isNotEmpty()) {
        assertEquals(array.size, chained.firstOrNull()?.remaining())
        assertThat(chained.firstOrNull(), ByteBufferMatcher(ByteBuffer.wrap(array)))
        assertEquals(1, chained.take(2).count())
      }
    }

    validateArrayInternal(array, direct = true)
    validateArrayInternal(array, direct = false)
  }
}
