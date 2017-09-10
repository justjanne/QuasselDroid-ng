package de.kuschku.quasseldroid_ng.protocol.primitive.serializer

import de.kuschku.quasseldroid_ng.protocol.Quassel_Features
import de.kuschku.quasseldroid_ng.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object ShortSerializer : Serializer<Short> {
  override fun serialize(buffer: ChainedByteBuffer, data: Short, features: Quassel_Features) {
    buffer.putShort(data)
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): Short {
    return buffer.short
  }
}
