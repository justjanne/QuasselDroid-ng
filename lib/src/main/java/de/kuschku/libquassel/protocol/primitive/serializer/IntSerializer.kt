package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object IntSerializer : Serializer<Int> {
  override fun serialize(buffer: ChainedByteBuffer, data: Int, features: Quassel_Features) {
    buffer.putInt(data)
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): Int {
    return buffer.int
  }
}
