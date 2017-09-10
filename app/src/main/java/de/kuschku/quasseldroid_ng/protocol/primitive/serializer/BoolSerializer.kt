package de.kuschku.quasseldroid_ng.protocol.primitive.serializer

import de.kuschku.quasseldroid_ng.protocol.Quassel_Features
import de.kuschku.quasseldroid_ng.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object BoolSerializer : Serializer<Boolean> {
  override fun serialize(buffer: ChainedByteBuffer, data: Boolean,
                         features: Quassel_Features) = buffer.put(if (data) {
    0x01
  } else {
    0x00
  }.toByte())

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features)
    = buffer.get() != 0x00.toByte()
}
