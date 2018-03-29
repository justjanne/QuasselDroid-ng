package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object ByteSerializer : Serializer<Byte> {
  override fun serialize(buffer: ChainedByteBuffer, data: Byte, features: QuasselFeatures) {
    buffer.put(data)
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): Byte {
    return buffer.get()
  }
}
