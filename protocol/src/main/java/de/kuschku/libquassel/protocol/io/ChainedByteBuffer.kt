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

class ChainedByteBuffer(private val bufferSize: Int = 1024, private val direct: Boolean = false) {
  private val bufferList: MutableList<ByteBuffer> = ArrayList()

  var size = 0
    private set

  private var currentBuffer = 0

  private fun allocate(size: Int) = when (direct) {
    true  -> ByteBuffer.allocateDirect(size)
    false -> ByteBuffer.allocate(size)
  }

  private fun ensureSpace(size: Int) {
    if (bufferList.isEmpty()) {
      bufferList.add(allocate(bufferSize))
    }
    if (bufferList[currentBuffer].remaining() < size) {
      currentBuffer += 1
    }
    if (currentBuffer == bufferList.size) {
      bufferList.add(allocate(bufferSize))
    }
    this.size += size
  }

  fun <T> withBuffer(length: Int = 0, f: (ByteBuffer) -> T) : T{
    ensureSpace(length)
    val buffer = bufferList.last()
    val positionBefore = buffer.position()
    val result = f(buffer)
    val positionAfter = buffer.position()
    size += (positionAfter - positionBefore)
    return result
  }

  fun put(value: Byte) {
    ensureSpace(1)

    bufferList.last().put(value)
  }

  fun putChar(value: Char) {
    ensureSpace(2)

    bufferList.last().putChar(value)
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
    ensureSpace(value.remaining())

    while (value.remaining() > 0) {
      copyData(value, bufferList.last())
    }
  }

  fun put(value: ByteArray) {
    value.forEach(this::put)
  }

  fun clear() {
    bufferList.clear()
    currentBuffer = 0
    size = 0
  }

  fun buffers() = sequence {
    for (buffer in bufferList) {
      buffer.flip()
      val position = buffer.position()
      val limit = buffer.limit()
      yield(buffer)
      buffer.position(position)
      buffer.limit(limit)
    }
  }

  fun toBuffer(): ByteBuffer {
    val byteBuffer = allocate(bufferSize * bufferList.size)
    for (buffer in bufferList) {
      buffer.flip()
      byteBuffer.put(buffer)
    }
    byteBuffer.flip()
    return byteBuffer
  }
}
