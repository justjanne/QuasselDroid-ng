package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.protocol.primitive.serializer.HandshakeVariantMapSerializer
import de.kuschku.libquassel.protocol.primitive.serializer.IntSerializer
import de.kuschku.libquassel.protocol.primitive.serializer.ProtocolInfoSerializer
import de.kuschku.libquassel.protocol.primitive.serializer.VariantListSerializer
import de.kuschku.libquassel.quassel.ProtocolFeature
import de.kuschku.libquassel.util.compatibility.CompatibilityUtils
import de.kuschku.libquassel.util.compatibility.HandlerService
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.*
import de.kuschku.libquassel.util.compatibility.log
import de.kuschku.libquassel.util.hasFlag
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

class CoreConnection(
  private val session: Session,
  private val address: SocketAddress,
  private val handlerService: HandlerService
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
    log(INFO, TAG, value.name)
    state.onNext(value)
  }

  private fun sendHandshake() {
    setState(ConnectionState.HANDSHAKE)

    IntSerializer.serialize(
      chainedBuffer,
      0x42b33f00 or session.clientData.protocolFeatures.toInt(),
      session.negotiatedFeatures
    )
    for (supportedProtocol in session.clientData.supportedProtocols) {
      IntSerializer.serialize(chainedBuffer, supportedProtocol.toInt(), session.negotiatedFeatures)
    }
    IntSerializer.serialize(chainedBuffer, 1 shl 31, session.negotiatedFeatures)
    channel?.write(chainedBuffer)
    channel?.flush()
  }

  private fun readHandshake() {
    sizeBuffer.clear()
    channel?.read(sizeBuffer)
    sizeBuffer.flip()
    val protocol = ProtocolInfoSerializer.deserialize(sizeBuffer, session.negotiatedFeatures)

    log(DEBUG, TAG, "Protocol negotiated $protocol")

    // Wrap socket in SSL context if ssl is enabled
    if (protocol.flags.hasFlag(ProtocolFeature.TLS)) {
      channel = channel?.withSSL(session.trustManager, address)
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
            clientVersion = session.clientData.identifier,
            buildDate = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss")
              .format(session.clientData.buildDate.atOffset(ZoneOffset.UTC)),
            clientFeatures = session.clientData.clientFeatures
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
      interrupt()
      handlerService.quit()
      setState(ConnectionState.DISCONNECTED)
    } catch (e: Throwable) {
      log(WARN, TAG, "Error encountered while closing connection", e)
    }
  }

  fun dispatch(message: HandshakeMessage) {
    handlerService.parse {
      try {
        val data = HandshakeMessage.serialize(message)
        handlerService.write(
          MessageRunnable(
            data, HandshakeVariantMapSerializer, chainedBuffer, channel,
            session.negotiatedFeatures
          )
        )
      } catch (e: Throwable) {
        log(WARN, TAG, "Error encountered while serializing handshake message", e)
      }
    }
  }

  fun dispatch(message: SignalProxyMessage) {
    handlerService.parse {
      try {
        val data = SignalProxyMessage.serialize(message)
        handlerService.write(
          MessageRunnable(
            data, VariantListSerializer, chainedBuffer, channel,
            session.negotiatedFeatures
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
      while (!isInterrupted) {
        sizeBuffer.clear()
        if (channel?.read(sizeBuffer) == -1)
          break
        sizeBuffer.flip()

        val size = IntSerializer.deserialize(sizeBuffer, session.negotiatedFeatures)
        if (size > 64 * 1024 * 1024)
          throw SocketException("Too large frame received: $size")
        val dataBuffer = ByteBuffer.allocateDirect(size)
        while (dataBuffer.position() < dataBuffer.limit() && channel?.read(dataBuffer) ?: -1 > 0) {
        }
        dataBuffer.flip()
        handlerService.parse {
          when (state.value) {
            ConnectionState.HANDSHAKE -> processHandshake(dataBuffer)
            else                      -> processSigProxy(dataBuffer)
          }
        }
      }
    } catch (e: Throwable) {
      log(WARN, TAG, "Error encountered in connection", e)
      setState(ConnectionState.DISCONNECTED)
    }
  }

  private fun processSigProxy(dataBuffer: ByteBuffer) {
    try {
      val msg = SignalProxyMessage.deserialize(
        VariantListSerializer.deserialize(dataBuffer, session.negotiatedFeatures)
      )
      handlerService.handle {
        try {
          session.handle(msg)
        } catch (e: Throwable) {
          log(WARN, TAG, "Error encountered while handling sigproxy message", e)
          log(WARN, TAG, msg.toString())
        }
      }
    } catch (e: Throwable) {
      log(WARN, TAG, "Error encountered while parsing sigproxy message", e)
      dataBuffer.hexDump()
    }
  }

  private fun processHandshake(dataBuffer: ByteBuffer) {
    try {
      val msg = HandshakeMessage.deserialize(
        HandshakeVariantMapSerializer.deserialize(dataBuffer, session.negotiatedFeatures)
      )
      try {
        session.handle(msg)
      } catch (e: Throwable) {
        log(WARN, TAG, "Error encountered while handling handshake message", e)
        log(WARN, TAG, msg.toString())
      }
    } catch (e: Throwable) {
      log(WARN, TAG, "Error encountered while parsing handshake message", e)
      dataBuffer.hexDump()
    }
  }
}
