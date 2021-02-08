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

import java.nio.ByteBuffer
import java.util.*

class ChainedByteBuffer(
  private val chunkSize: Int = 1024,
  private val direct: Boolean = false,
  private val limit: Long = 0,
) : Iterable<ByteBuffer> {
  private val bufferList: MutableList<ByteBuffer> = ArrayList()

  private var currentIndex = 0

  var size = 0
    private set

  private fun allocate(amount: Int): ByteBuffer {
    require(limit <= 0 || size + amount <= limit) {
      "Can not allocate $amount bytes, currently at $size, limit is $limit"
    }
    return if (direct) ByteBuffer.allocateDirect(amount)
    else ByteBuffer.allocate(amount)
  }

  private fun ensureSpace(requested: Int) {
    if (bufferList.lastOrNull()?.remaining() ?: 0 < requested) {
      bufferList.add(allocate(chunkSize))
    }
    size += requested
  }

  fun put(value: Byte) {
    ensureSpace(1)

    bufferList.last().put(value)
  }

  fun putShort(value: Short) {
    ensureSpace(2)

    bufferList.last().putShort(value)
  }

  fun putInt(value: Int) {
    ensureSpace(4)

    bufferList.last().putInt(value)
  }

  fun putLong(value: Long) {
    ensureSpace(8)

    bufferList.last().putLong(value)
  }

  fun putFloat(value: Float) {
    ensureSpace(4)

    bufferList.last().putFloat(value)
  }

  fun putDouble(value: Double) {
    ensureSpace(8)

    bufferList.last().putDouble(value)
  }

  fun put(value: ByteBuffer) {
    while (value.hasRemaining()) {
      val requested = minOf(value.remaining(), chunkSize)
      if (bufferList.lastOrNull()?.hasRemaining() != true) {
        ensureSpace(requested)
      } else {
        ensureSpace(minOf(bufferList.last().remaining(), requested))
      }

      copyData(value, bufferList.last(), requested)
    }
  }

  fun put(value: ByteArray) {
    value.forEach(this::put)
  }

  fun clear() {
    bufferList.clear()
    size = 0
  }

  override fun iterator() = ChainedByteBufferIterator(this)

  fun toBuffer(): ByteBuffer {
    val byteBuffer = allocate(chunkSize * bufferList.size)
    for (buffer in iterator()) {
      byteBuffer.put(buffer)
    }
    byteBuffer.flip()
    return byteBuffer
  }

  class ChainedByteBufferIterator(
    private val buffer: ChainedByteBuffer
  ) : Iterator<ByteBuffer> {
    private var index = 0

    override fun hasNext() =
      index < buffer.bufferList.size

    override fun next(): ByteBuffer =
      buffer.bufferList[index++].duplicate().flip()
  }
}
