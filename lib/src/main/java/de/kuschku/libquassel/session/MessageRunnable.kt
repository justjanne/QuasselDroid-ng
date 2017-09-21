package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.protocol.primitive.serializer.Serializer
import de.kuschku.libquassel.util.LoggingHandler.LogLevel.WARN
import de.kuschku.libquassel.util.helpers.write
import de.kuschku.libquassel.util.log
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import de.kuschku.libquassel.util.nio.WrappedChannel
import java.nio.ByteBuffer

class MessageRunnable<T>(
  private val data: T,
  private val serializer: Serializer<T>,
  private val chainedBuffer: ChainedByteBuffer,
  private val channel: WrappedChannel?,
  private val features: Quassel_Features
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
    } catch (e: Throwable) {
      log(WARN, "MessageDispatching", e)
    }
  }
}
