package de.kuschku.quasseldroid_ng.protocol.primitive.serializer

import de.kuschku.quasseldroid_ng.protocol.Quassel_Features
import de.kuschku.quasseldroid_ng.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object LongSerializer : Serializer<Long> {
  override fun serialize(buffer: ChainedByteBuffer, data: Long, features: Quassel_Features) {
    buffer.putLong(data)
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): Long {
    return buffer.long
  }
}
