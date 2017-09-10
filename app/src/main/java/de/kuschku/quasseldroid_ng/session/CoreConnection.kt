package de.kuschku.quasseldroid_ng.session

import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import de.kuschku.quasseldroid_ng.protocol.HandshakeMessage
import de.kuschku.quasseldroid_ng.protocol.Quassel_Feature
import de.kuschku.quasseldroid_ng.protocol.Quassel_Features
import de.kuschku.quasseldroid_ng.protocol.SignalProxyMessage
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.HandshakeVariantMapSerializer
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.IntSerializer
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.ProtocolSerializer
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.VariantListSerializer
import de.kuschku.quasseldroid_ng.quassel.ProtocolFeature
import de.kuschku.quasseldroid_ng.util.CompatibilityUtils
import de.kuschku.quasseldroid_ng.util.hasFlag
import de.kuschku.quasseldroid_ng.util.helpers.*
import de.kuschku.quasseldroid_ng.util.nio.ChainedByteBuffer
import de.kuschku.quasseldroid_ng.util.nio.WrappedChannel
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.lang.Thread.UncaughtExceptionHandler
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer

class CoreConnection(
  private val session: Session,
  private val address: SocketAddress
) : Thread() {
  private val parseThread = HandlerThread("parse", Process.THREAD_PRIORITY_BACKGROUND)
  private val writeThread = HandlerThread("write", Process.THREAD_PRIORITY_BACKGROUND)
  private val backendThread = HandlerThread("backend", Process.THREAD_PRIORITY_BACKGROUND)
  private lateinit var parseHandler: Handler
  private lateinit var writeHandler: Handler
  private lateinit var backendHandler: Handler

  private val exceptionHandler = UncaughtExceptionHandler { thread, throwable ->
    Logger.error(thread.name, "", throwable)
  }

  private val sizeBuffer = ByteBuffer.allocateDirect(4)
  private val chainedBuffer = ChainedByteBuffer(direct = true)
  val liveState = MutableLiveData<ConnectionState>()

  init {
    liveState.value = ConnectionState.DISCONNECTED
  }

  var state = ConnectionState.DISCONNECTED
    set(value) {
      field = value
      Logger.debug("CoreConnection", "Connection state changed to $state")
      liveState.postValue(value)
    }

  private var channel: WrappedChannel? = null

  private fun connect() {
    state = ConnectionState.CONNECTING
    val socket = Socket()
    if (CompatibilityUtils.deviceSupportsKeepAlive())
      socket.keepAlive = true
    socket.connect(address.data(), 10_000)
    channel = WrappedChannel.ofSocket(socket)
    parseThread.uncaughtExceptionHandler = exceptionHandler
    writeThread.uncaughtExceptionHandler = exceptionHandler
    backendThread.uncaughtExceptionHandler = exceptionHandler
    parseThread.start()
    writeThread.start()
    backendThread.start()
    parseHandler = Handler(parseThread.looper)
    writeHandler = Handler(writeThread.looper)
    backendHandler = Handler(backendThread.looper)
  }

  private fun sendHandshake() {
    state = ConnectionState.HANDSHAKE
    IntSerializer.serialize(chainedBuffer,
                            0x42b33f00 or session.clientData.protocolFeatures.toInt(),
                            session.coreFeatures)
    for (supportedProtocol in session.clientData.supportedProtocols) {
      IntSerializer.serialize(chainedBuffer, supportedProtocol.toInt(), session.coreFeatures)
    }
    IntSerializer.serialize(chainedBuffer, 1 shl 31, session.coreFeatures)
    channel?.write(chainedBuffer)
    channel?.flush()
  }

  private fun readHandshake() {
    sizeBuffer.clear()
    channel?.read(sizeBuffer)
    sizeBuffer.flip()
    val protocol = ProtocolSerializer.deserialize(sizeBuffer, session.coreFeatures)

    println(protocol)

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
        dispatch(HandshakeMessage.ClientInit(
          clientVersion = session.clientData.identifier,
          buildDate = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm:ss")
            .format(session.clientData.buildDate.atOffset(ZoneOffset.UTC)),
          clientFeatures = Quassel_Features.of(*Quassel_Feature.values())
        ))
      }
      else -> {
        throw IllegalArgumentException("Invalid Protocol Version: $protocol")
      }
    }
  }

  fun close() {
    interrupt()
    parseThread.quit()
    writeThread.quit()
    backendThread.quit()
    val thread = Thread {
      try {
        channel?.close()
      } catch (e: Throwable) {
        Logger.warn("ConnectionClosing", "", e)
      }
    }
    thread.start()
    thread.join()
  }

  fun dispatch(message: HandshakeMessage) {
    parseHandler.post {
      try {
        val data = HandshakeMessage.serialize(message)
        writeHandler.post(
          MessageRunnable(data, HandshakeVariantMapSerializer, chainedBuffer, channel,
                          session.coreFeatures)
        )
      } catch (e: Throwable) {
        Logger.warn("HandshakeSerializing", "", e)
      }
    }
  }

  fun dispatch(message: SignalProxyMessage) {
    parseHandler.post {
      try {
        val data = SignalProxyMessage.serialize(message)
        writeHandler.post(
          MessageRunnable(data, VariantListSerializer, chainedBuffer, channel, session.coreFeatures)
        )
      } catch (e: Throwable) {
        Logger.warn("MessageSerializing", "", e)
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
        channel?.read(sizeBuffer)
        sizeBuffer.flip()

        val size = IntSerializer.deserialize(sizeBuffer, session.coreFeatures)
        if (size > 64 * 1024 * 1024)
          throw SocketException("Too large frame received: $size")
        val dataBuffer = ByteBuffer.allocateDirect(size)
        while (dataBuffer.position() < dataBuffer.limit() && channel?.read(dataBuffer) ?: -1 > 0) {
        }
        dataBuffer.flip()
        parseHandler.post {
          when (state) {
            ConnectionState.HANDSHAKE -> {
              try {
                val msg = HandshakeMessage.deserialize(
                  HandshakeVariantMapSerializer.deserialize(dataBuffer, session.coreFeatures)
                )
                try {
                  session.handle(msg)
                } catch (e: Throwable) {
                  Logger.warn("HandshakeHandling", "", e)
                }
              } catch (e: Throwable) {
                Logger.warn("HandshakeParsing", "", e)
              }
            }
            else                      ->
              try {
                val msg = SignalProxyMessage.deserialize(
                  VariantListSerializer.deserialize(dataBuffer, session.coreFeatures)
                )
                backendHandler.post {
                  try {
                    session.handle(msg)
                  } catch (e: Throwable) {
                    Logger.warn("MessageHandling", "", e)
                  }
                }
              } catch (e: Throwable) {
                Logger.warn("MessageParsing", "", e)
              }
          }
        }
      }
    } catch (e: Throwable) {
      Logger.warn("CoreConnection", "", e)
    }
  }
}
