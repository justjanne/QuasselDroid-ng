package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object ByteArraySerializer : Serializer<ByteBuffer?> {
  override fun serialize(buffer: ChainedByteBuffer, data: ByteBuffer?, features: Quassel_Features) {
    if (data == null) {
      IntSerializer.serialize(buffer, -1, features)
    } else {
      IntSerializer.serialize(buffer, data.remaining(), features)
      buffer.put(data)
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): ByteBuffer? {
    val len = IntSerializer.deserialize(buffer, features)
    return if (len == -1) {
      null
    } else {
      val result = ByteBuffer.allocate(len)
      while (result.hasRemaining())
        result.put(buffer.get())
      result.flip()
      result
    }
  }
}
