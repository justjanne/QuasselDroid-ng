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

package de.kuschku.quasseldroid.protocol.io

import de.kuschku.libquassel.protocol.io.ChainedByteBuffer
import de.kuschku.quasseldroid.util.TlsInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runInterruptible
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import javax.net.ssl.SSLContext

class CoroutineChannel {
  private lateinit var channel: StreamChannel
  private val writeContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
  private val readContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
  private val _tlsInfo = MutableStateFlow<TlsInfo?>(null)
  val tlsInfo: StateFlow<TlsInfo?> get() = _tlsInfo

  suspend fun connect(
    address: InetSocketAddress,
    timeout: Int = 0,
    keepAlive: Boolean = false,
  ) = runInterruptible(Dispatchers.IO) {
    val socket = Socket()
    socket.keepAlive = keepAlive
    socket.connect(address, timeout)
    this.channel = StreamChannel(socket)
  }

  fun enableCompression() {
    channel = channel.withCompression()
  }

  suspend fun enableTLS(context: SSLContext) {
    channel = runInterruptible(writeContext) {
      channel.withTLS(context)
    }
    _tlsInfo.emit(channel.tlsInfo())
  }

  suspend fun read(buffer: ByteBuffer): Int = runInterruptible(readContext) {
    this.channel.read(buffer)
  }

  suspend fun write(buffer: ByteBuffer): Int = runInterruptible(writeContext) {
    this.channel.write(buffer)
  }

  suspend fun write(chainedBuffer: ChainedByteBuffer) {
    for (buffer in chainedBuffer.iterator()) {
      write(buffer)
    }
  }

  suspend fun flush() = runInterruptible(writeContext) {
    this.channel.flush()
  }
}
