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

package de.kuschku.libquassel.connection

import de.kuschku.libquassel.protocol.ClientData
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.protocol.primitive.serializer.HandshakeVariantMapSerializer
import de.kuschku.libquassel.protocol.primitive.serializer.IntSerializer
import de.kuschku.libquassel.protocol.primitive.serializer.ProtocolInfoSerializer
import de.kuschku.libquassel.protocol.primitive.serializer.VariantListSerializer
import de.kuschku.libquassel.quassel.ProtocolFeature
import de.kuschku.libquassel.session.ProtocolHandler
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.compatibility.CompatibilityUtils
import de.kuschku.libquassel.util.compatibility.HandlerService
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.DEBUG
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.WARN
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helpers.hexDump
import de.kuschku.libquassel.util.helpers.write
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import de.kuschku.libquassel.util.nio.WrappedChannel
import io.reactivex.subjects.BehaviorSubject
import java.io.Closeable
import java.lang.Thread.UncaughtExceptionHandler
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager

class CoreConnection(
  private val clientData: ClientData,
  private val features: Features,
  private val trustManager: X509TrustManager,
  private val hostnameVerifier: HostnameVerifier,
  private val address: SocketAddress,
  private val handlerService: HandlerService
) : Thread(), Closeable {
  companion object {
    private const val TAG = "CoreConnection"
  }

  private var handler: ProtocolHandler? = null
  private var securityExceptionCallback: ((QuasselSecurityException) -> Unit)? = null
  private var exceptionCallback: ((Throwable) -> Unit)? = null

  fun setHandlers(handler: ProtocolHandler?,
                  securityExceptionCallback: ((QuasselSecurityException) -> Unit)?,
                  exceptionCallback: ((Throwable) -> Unit)?) {
    this.handler = handler
    this.securityExceptionCallback = securityExceptionCallback
    this.exceptionCallback = exceptionCallback
  }

  private val exceptionHandler = UncaughtExceptionHandler { thread, throwable ->
    log(WARN, TAG, thread.name, throwable)
  }

  private val sizeBuffer = ByteBuffer.allocateDirect(4)
  private val chainedBuffer = ChainedByteBuffer(direct = true)

  val state: BehaviorSubject<ConnectionState> = BehaviorSubject.createDefault(
    ConnectionState.DISCONNECTED
  )

  private var channel: WrappedChannel? = null
    set(value) {
      field = value
      sslSession.onNext(Optional.ofNullable(value?.sslSession))
    }

  private fun connect() {
    setState(ConnectionState.CONNECTING)
    val socket = Socket()
    if (CompatibilityUtils.supportsKeepAlive)
      socket.keepAlive = true
    socket.connect(address.data(), 10_000)
    handlerService.exceptionHandler = exceptionHandler
    channel = WrappedChannel.ofSocket(socket)
  }

  fun setState(value: ConnectionState) {
    val current = state.value
    if (current != ConnectionState.CLOSED) {
      log(DEBUG, TAG, value.name)
      state.onNext(value)
    } else if (current != value) {
      log(WARN, TAG, "Trying to set state while closed: $value", Throwable())
    }
  }

  private fun sendHandshake() {
    setState(ConnectionState.HANDSHAKE)
    IntSerializer.serialize(
      chainedBuffer,
      0x42b33f00 or clientData.protocolFeatures.toInt(),
      features.negotiated
    )
    for (supportedProtocol in clientData.supportedProtocols) {
      IntSerializer.serialize(chainedBuffer, supportedProtocol.toInt(), features.negotiated)
    }
    IntSerializer.serialize(chainedBuffer, 1 shl 31, features.negotiated)
    channel?.write(chainedBuffer)
    channel?.flush()
  }

  private fun readHandshake() {
    sizeBuffer.clear()
    channel?.read(sizeBuffer)
    sizeBuffer.flip()
    val protocol = ProtocolInfoSerializer.deserialize(sizeBuffer, features.negotiated)

    log(DEBUG, TAG, "Protocol negotiated $protocol")

    // Wrap socket in SSL context if ssl is enabled
    if (protocol.flags.hasFlag(ProtocolFeature.TLS)) {
      channel = channel?.withSSL(trustManager, hostnameVerifier, address)
    }

    // Wrap socket in deflater if compression is enabled
    if (protocol.flags.hasFlag(ProtocolFeature.Compression)) {
      channel = channel?.withCompression()
    }

    // Initialize remote peer
    when (protocol.version.toInt()) {
      0x02 -> {
        // Send client clientData to core
        dispatch(
          HandshakeMessage.ClientInit(
            clientVersion = clientData.identifier,
            buildDate = clientData.buildDate.epochSecond.toString(),
            clientFeatures = clientData.clientFeatures.toInt(),
            featureList = clientData.clientFeatures.toStringList()
          )
        )
      }
      else -> {
        throw ProtocolVersionException(protocol)
      }
    }
  }

  override fun close() {
    try {
      setState(ConnectionState.CLOSED)
      channel?.flush()
      channel?.close()
      setHandlers(null, null, null)
      interrupt()
    } catch (e: Throwable) {
      log(WARN, TAG, "Error encountered while closing connection: $e")
    }
  }

  fun dispatch(message: HandshakeMessage) {
    handlerService.serialize {
      try {
        val data = HandshakeMessage.serialize(message)
        handlerService.write(
          MessageRunnable(
            data, HandshakeVariantMapSerializer, chainedBuffer, channel,
            features.negotiated
          )
        )
      } catch (e: Throwable) {
        log(WARN, TAG, "Error encountered while serializing handshake message", e)
      }
    }
  }

  fun dispatch(message: SignalProxyMessage) {
    handlerService.serialize {
      try {
        val data = SignalProxyMessage.serialize(message)
        handlerService.write(
          MessageRunnable(
            data, VariantListSerializer, chainedBuffer, channel,
            features.negotiated
          )
        )
      } catch (e: Throwable) {
        log(WARN, TAG, "Error encountered while serializing sigproxy message", e)
      }
    }
  }

  override fun run() {
    try {
      connect()
      sendHandshake()
      readHandshake()
      while (!isInterrupted && state != ConnectionState.CLOSED) {
        sizeBuffer.clear()
        if (channel?.read(sizeBuffer) == -1)
          break
        sizeBuffer.flip()

        val size = IntSerializer.deserialize(sizeBuffer, features.negotiated)
        if (size > 64 * 1024 * 1024)
          throw SocketException("Too large frame received: $size")
        val dataBuffer = ByteBuffer.allocateDirect(size)
        while (dataBuffer.position() < dataBuffer.limit() && channel?.read(dataBuffer) ?: -1 > 0) {
        }
        dataBuffer.flip()

        handlerService.deserialize {
          when (state.value) {
            ConnectionState.CLOSED    ->
              // Connection closed, do nothing
              Unit
            ConnectionState.CONNECTING,
            ConnectionState.HANDSHAKE ->
              processHandshake(dataBuffer)
            else                      ->
              processSigProxy(dataBuffer)
          }
        }
      }
      channel?.close()
    } catch (e: Throwable) {
      val closed = state.value == ConnectionState.CLOSED

      var cause: Throwable? = e
      var exception: QuasselSecurityException?
      do {
        exception = cause as? QuasselSecurityException
        cause = cause?.cause
      } while (cause != null && exception == null)
      if (exception != null) {
        close()
        securityExceptionCallback?.invoke(exception)
      } else {
        if (!closed) {
          log(WARN, TAG, "Error encountered in connection", e)
          log(WARN, TAG, "Last sent message: ${MessageRunnable.lastSent.get()}")
          exceptionCallback?.invoke(e)
        }
        close()
      }
    }
  }

  private fun processSigProxy(dataBuffer: ByteBuffer) = handlerService.deserialize {
    try {
      val msg = SignalProxyMessage.deserialize(
        VariantListSerializer.deserialize(dataBuffer, features.negotiated)
      )
      handlerService.backend {
        try {
          handler?.handle(msg)
        } catch (e: Throwable) {
          log(WARN, TAG, "Error encountered while handling sigproxy message", e)
          log(WARN, TAG, msg.toString())
        }
      }

    } catch (e: Throwable) {
      log(WARN,
          TAG, "Error encountered while parsing sigproxy message", e)
      dataBuffer.hexDump()
    }
  }

  private fun processHandshake(dataBuffer: ByteBuffer) = try {
    val msg = HandshakeMessage.deserialize(
      HandshakeVariantMapSerializer.deserialize(dataBuffer, features.negotiated)
    )
    try {
      handler?.handle(msg)
    } catch (e: Throwable) {
      log(WARN,
          TAG, "Error encountered while handling handshake message", e)
      log(WARN, TAG, msg.toString())
    }
  } catch (e: Throwable) {
    log(
      WARN,
      TAG, "Error encountered while parsing handshake message", e
    )
  }


  val sslSession: BehaviorSubject<Optional<SSLSession>> = BehaviorSubject.createDefault(Optional.empty())
}
