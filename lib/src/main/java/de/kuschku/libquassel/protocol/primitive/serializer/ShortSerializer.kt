package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object ShortSerializer : Serializer<Short> {
  override fun serialize(buffer: ChainedByteBuffer, data: Short, features: QuasselFeatures) {
    buffer.putShort(data)
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): Short {
    return buffer.short
  }
}
