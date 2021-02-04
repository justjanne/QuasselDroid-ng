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

package de.kuschku.quasseldroid.util

import de.kuschku.quasseldroid.protocol.ChainedByteBuffer
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.util.concurrent.Executors

class CoroutineChannel {
  private lateinit var channel: StreamChannel
  private val writeContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
  private val readContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

  suspend fun connect(address: InetSocketAddress) = runInterruptible(writeContext) {
    this.channel = StreamChannel(Socket(address.address, address.port))
  }

  suspend fun read(buffer: ByteBuffer): Int = runInterruptible(readContext) {
    this.channel.read(buffer)
  }

  suspend fun write(buffer: ByteBuffer): Int = runInterruptible(writeContext) {
    this.channel.write(buffer)
  }

  suspend fun write(chainedBuffer: ChainedByteBuffer) {
    for (buffer in chainedBuffer.buffers()) {
      write(buffer)
    }
  }
}
