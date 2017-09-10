package de.kuschku.quasseldroid_ng.session

import de.kuschku.quasseldroid_ng.protocol.Quassel_Features
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.Serializer
import de.kuschku.quasseldroid_ng.util.helpers.Logger
import de.kuschku.quasseldroid_ng.util.helpers.warn
import de.kuschku.quasseldroid_ng.util.helpers.write
import de.kuschku.quasseldroid_ng.util.nio.ChainedByteBuffer
import de.kuschku.quasseldroid_ng.util.nio.WrappedChannel
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
      Logger.warn("MessageDispatching", "", e)
    }
  }
}
