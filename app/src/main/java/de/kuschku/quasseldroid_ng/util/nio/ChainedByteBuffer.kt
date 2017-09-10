package de.kuschku.quasseldroid_ng.util.nio

import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel
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
    while (value.remaining() > 8) {
      putLong(value.long)
    }
    while (value.hasRemaining()) {
      put(value.get())
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

  fun write(channel: WritableByteChannel) {
    for (buffer in bufferList) {
      buffer.flip()
      channel.write(buffer)
    }
  }

  fun toBuffer(): ByteBuffer {
    val byteBuffer = allocate(size)
    for (buffer in bufferList) {
      buffer.flip()
      byteBuffer.put(buffer)
    }
    byteBuffer.flip()
    return byteBuffer
  }
}
