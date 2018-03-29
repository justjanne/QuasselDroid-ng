package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object HandshakeVariantMapSerializer : Serializer<QVariantMap> {
  override fun serialize(buffer: ChainedByteBuffer, data: QVariantMap, features: QuasselFeatures) {
    IntSerializer.serialize(buffer, data.size * 2, features)
    data.entries.forEach { (key, value) ->
      VariantSerializer.serialize(buffer, QVariant.of(key, Type.QString), features)
      VariantSerializer.serialize(buffer, value, features)
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): QVariantMap {
    val range = 0 until IntSerializer.deserialize(buffer, features) / 2
    val pairs = range.map {
      val keyRaw: ByteBuffer? = VariantSerializer.deserialize(buffer, features).value()
      val key: String? = if (keyRaw != null) {
        StringSerializer.UTF8.deserializeAll(keyRaw)
      } else {
        null
      }
      val value = VariantSerializer.deserialize(buffer, features)
      Pair(key ?: "", value)
    }
    val pairArray = pairs.toTypedArray()
    return mapOf(*pairArray)
  }
}
