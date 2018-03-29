package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object VoidSerializer : Serializer<Any?> {
  override fun serialize(buffer: ChainedByteBuffer, data: Any?, features: QuasselFeatures) {
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): Any? {
    return null
  }
}
