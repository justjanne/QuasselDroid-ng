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
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.io.Closeable
import java.lang.Thread.UncaughtExceptionHandler
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import javax.net.ssl.X509TrustManager

class CoreConnection(
  private val handler: ProtocolHandler,
  private val clientData: ClientData,
  private val features: Features,
  private val trustManager: X509TrustManager,
  private val hostnameVerifier: HostnameVerifier,
  private val address: SocketAddress,
  private val handlerService: HandlerService,
  private val securityExceptionCallback: (QuasselSecurityException) -> Unit
) : Thread(), Closeable {
  companion object {
    private const val TAG = "CoreConnection"
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
    log(DEBUG, TAG, value.name)
    state.onNext(value)
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
            buildDate = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss")
              .format(clientData.buildDate.atOffset(ZoneOffset.UTC)),
            clientFeatures = clientData.clientFeatures.toInt(),
            featureList = clientData.clientFeatures.toStringList()
          )
        )
      }
      else -> {
        throw IllegalArgumentException("Invalid Protocol Version: $protocol")
      }
    }
  }

  override fun close() {
    try {
      setState(ConnectionState.CLOSED)
      channel?.flush()
      channel?.close()
      interrupt()
    } catch (e: Throwable) {
      log(WARN, TAG, "Error encountered while closing connection", e)
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
        log(WARN,
            TAG, "Error encountered while serializing handshake message", e)
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
        log(WARN,
            TAG, "Error encountered while serializing sigproxy message", e)
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
      var cause: Throwable? = e
      var exception: QuasselSecurityException?
      do {
        exception = cause as? QuasselSecurityException
        cause = cause?.cause
      } while (cause != null && exception == null)
      if (exception != null) {
        close()
        securityExceptionCallback(exception)
      } else {
        log(WARN, TAG, "Error encountered in connection", e)
        log(WARN, TAG, "Last sent message: ${MessageRunnable.lastSent.get()}")
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
          handler.handle(msg)
        } catch (e: Throwable) {
          log(WARN,
              TAG, "Error encountered while handling sigproxy message", e)
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
      handler.handle(msg)
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

  val sslSession
    get() = channel?.sslSession
}
