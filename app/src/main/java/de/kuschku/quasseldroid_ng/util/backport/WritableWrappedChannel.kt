package de.kuschku.quasseldroid_ng.util.backport

import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel
import java.nio.channels.spi.AbstractInterruptibleChannel


class WritableWrappedChannel(
  private var backingStream: OutputStream
) : AbstractInterruptibleChannel(), WritableByteChannel {
  private val buffer = ByteBuffer.allocate(PAGE_SIZE)
  private val lock = Any()

  override fun write(src: ByteBuffer): Int {
    val totalData = src.remaining()
    var remainingData = totalData

    synchronized(lock) {
      while (remainingData > 0) {
        // Data to be written, always the minimum of available data and the page size
        val writtenData = Math.min(remainingData, PAGE_SIZE)

        // Set new buffer info
        buffer.clear()
        buffer.limit(writtenData)
        // Read data into buffer
        buffer.put(src)

        try {
          // begin blocking operation, this handles interruption etc. properly
          begin()
          // Write data to backing stream
          backingStream.write(buffer.array(), 0, writtenData)
        } finally {
          // end blocking operation, this handles interruption etc. properly
          end(writtenData > 0)
        }

        // add written amount to total
        remainingData -= writtenData
      }

      return (totalData - remainingData)
    }
  }

  override fun implCloseChannel() = backingStream.close()

  companion object {
    private val PAGE_SIZE = 8192
  }
}
