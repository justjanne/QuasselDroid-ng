package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object VariantListSerializer : Serializer<QVariantList> {
  override fun serialize(buffer: ChainedByteBuffer, data: QVariantList,
                         features: Quassel_Features) {
    IntSerializer.serialize(buffer, data.size, features)
    data.forEach {
      VariantSerializer.serialize(buffer, it, features)
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): QVariantList {
    val length = IntSerializer.deserialize(buffer, features)
    val result = mutableListOf<QVariant_>()
    for (i in 0 until length) {
      result.add(VariantSerializer.deserialize(buffer, features))
    }
    return result
  }

}
