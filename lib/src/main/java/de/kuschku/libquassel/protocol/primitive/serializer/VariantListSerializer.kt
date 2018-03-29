package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object VariantListSerializer : Serializer<QVariantList> {
  override fun serialize(buffer: ChainedByteBuffer, data: QVariantList, features: QuasselFeatures) {
    IntSerializer.serialize(buffer, data.size, features)
    data.forEach {
      VariantSerializer.serialize(buffer, it, features)
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): QVariantList {
    val length = IntSerializer.deserialize(buffer, features)
    val result = mutableListOf<QVariant_>()
    for (i in 0 until length) {
      result.add(VariantSerializer.deserialize(buffer, features))
    }
    return result
  }

}
