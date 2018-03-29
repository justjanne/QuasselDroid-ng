package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object BoolSerializer : Serializer<Boolean> {
  override fun serialize(buffer: ChainedByteBuffer, data: Boolean, features: QuasselFeatures) =
    buffer.put((if (data) 0x01 else 0x00).toByte())

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures) =
    buffer.get() != 0x00.toByte()
}
