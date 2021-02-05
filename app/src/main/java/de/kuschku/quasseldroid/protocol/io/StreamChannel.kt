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

import de.kuschku.quasseldroid.util.TlsInfo
import java.io.Flushable
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel
import java.nio.channels.InterruptibleChannel
import java.util.zip.InflaterInputStream
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket

class StreamChannel constructor(
  private val socket: Socket,
  private val inputStream: InputStream = socket.getInputStream(),
  private val outputStream: OutputStream = socket.getOutputStream(),
) : Flushable by outputStream, ByteChannel, InterruptibleChannel {
  private val input = ReadableWrappedChannel(inputStream)
  private val output = WritableWrappedChannel(outputStream)

  fun tlsInfo(): TlsInfo? {
    val sslSocket = socket as? SSLSocket ?: return null
    return TlsInfo.ofSession(sslSocket.session)
  }

  fun withCompression(): StreamChannel {
    return StreamChannel(
      socket,
      InflaterInputStream(inputStream),
      FixedDeflaterOutputStream(outputStream),
    )
  }

  fun withTLS(
    context: SSLContext,
  ): StreamChannel {
    val sslSocket = context.socketFactory.createSocket(
      this.socket,
      this.socket.inetAddress.hostAddress,
      this.socket.port,
      true
    ) as SSLSocket
    sslSocket.useClientMode = true
    sslSocket.startHandshake()
    return StreamChannel(sslSocket)
  }

  override fun close() {
    input.close()
    output.close()
    socket.close()
  }

  override fun isOpen(): Boolean {
    return !socket.isClosed
  }

  override fun read(dst: ByteBuffer): Int {
    return input.read(dst)
  }

  override fun write(src: ByteBuffer): Int {
    return output.write(src)
  }
}
