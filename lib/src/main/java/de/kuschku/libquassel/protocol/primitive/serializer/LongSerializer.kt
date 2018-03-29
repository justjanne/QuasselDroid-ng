package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object LongSerializer : Serializer<Long> {
  override fun serialize(buffer: ChainedByteBuffer, data: Long, features: QuasselFeatures) {
    buffer.putLong(data)
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): Long {
    return buffer.long
  }
}
