package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.primitive.serializer.Serializer
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.WARN
import de.kuschku.libquassel.util.helpers.write
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import de.kuschku.libquassel.util.nio.WrappedChannel
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicReference

class MessageRunnable<T>(
  private val data: T,
  private val serializer: Serializer<T>,
  private val chainedBuffer: ChainedByteBuffer,
  private val channel: WrappedChannel?,
  private val features: QuasselFeatures
) : () -> Unit {
  override fun invoke() {
    try {
      serializer.serialize(chainedBuffer, data, features)
      val sizeBuffer = ByteBuffer.allocateDirect(4)
      sizeBuffer.putInt(chainedBuffer.size)
      sizeBuffer.flip()
      channel?.write(sizeBuffer)
      channel?.write(chainedBuffer)
      channel?.flush()
      lastSent.set(data)
    } catch (e: Throwable) {
      log(WARN, "MessageDispatching", e)
    }
  }

  companion object {
    val lastSent = AtomicReference<Any>()
  }
}
