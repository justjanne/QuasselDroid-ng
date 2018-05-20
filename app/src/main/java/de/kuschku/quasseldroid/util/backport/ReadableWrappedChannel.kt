/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util.backport

import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.ERROR
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.channels.spi.AbstractInterruptibleChannel

class ReadableWrappedChannel(
  private var backingStream: InputStream
) : AbstractInterruptibleChannel(), ReadableByteChannel {
  private val buffer = ByteBuffer.allocate(PAGE_SIZE)
  private val lock = Any()

  override fun read(dst: ByteBuffer): Int {
    val totalData = dst.remaining()
    var remainingData = totalData

    // used to mark if we’ve already read any data. This is used to ensure that we only ever block
    // once for reading
    var hasRead = false

    synchronized(lock) {
      // Only read as long as we have content to read, and until we’ve blocked at most once
      while (remainingData > 0 && !(hasRead && backingStream.available() == 0)) {
        // Data to be read, always the minimum of available data and the page size
        val toReadOnce = Math.min(remainingData, PAGE_SIZE)
        var readData = 0

        try {
          // begin blocking operation, this handles interruption etc. properly
          begin()
          // prepare buffer for reading by resetting position and limit
          buffer.clear()
          // read data into buffer
          readData = backingStream.read(buffer.array(), 0, toReadOnce)
          // accurately set buffer info
          buffer.position(readData)
        } finally {
          // end blocking operation, this handles interruption etc. properly
          end(readData > 0)
        }

        if (readData <= 0) {
          log(ERROR, "ReadableWrappedChannel", "Read: $readData")
        }

        // read is negative if no data was read, in that case, terminate
        if (readData < 0)
          break

        // add read amount to total
        remainingData -= readData
        // mark that we’ve read data (to only block once)
        hasRead = true

        // flip buffer to prepare for reading
        buffer.flip()
        dst.put(buffer)
      }
    }

    return (totalData - remainingData)
  }

  override fun implCloseChannel() = backingStream.close()

  companion object {
    private const val PAGE_SIZE = 8192
  }
}
