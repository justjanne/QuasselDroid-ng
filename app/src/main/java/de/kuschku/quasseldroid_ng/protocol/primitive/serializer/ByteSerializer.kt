package de.kuschku.quasseldroid_ng.protocol.primitive.serializer

import de.kuschku.quasseldroid_ng.protocol.Quassel_Features
import de.kuschku.quasseldroid_ng.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object ByteSerializer : Serializer<Byte> {
  override fun serialize(buffer: ChainedByteBuffer, data: Byte, features: Quassel_Features) {
    buffer.put(data)
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): Byte {
    return buffer.get()
  }
}
