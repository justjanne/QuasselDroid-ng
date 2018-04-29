/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
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
    private const val PAGE_SIZE = 8192
  }
}
