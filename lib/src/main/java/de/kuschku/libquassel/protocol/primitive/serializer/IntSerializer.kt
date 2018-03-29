package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object IntSerializer : Serializer<Int> {
  override fun serialize(buffer: ChainedByteBuffer, data: Int, features: QuasselFeatures) {
    buffer.putInt(data)
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): Int {
    return buffer.int
  }
}
