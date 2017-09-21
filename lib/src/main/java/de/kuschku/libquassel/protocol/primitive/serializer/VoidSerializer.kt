package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object VoidSerializer : Serializer<Any?> {
  override fun serialize(buffer: ChainedByteBuffer, data: Any?, features: Quassel_Features) {
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): Any? {
    return null
  }
}
