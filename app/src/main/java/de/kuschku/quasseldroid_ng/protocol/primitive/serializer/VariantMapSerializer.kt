package de.kuschku.quasseldroid_ng.protocol.primitive.serializer

import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.Quassel_Features
import de.kuschku.quasseldroid_ng.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object VariantMapSerializer : Serializer<QVariantMap> {
  override fun serialize(buffer: ChainedByteBuffer, data: QVariantMap, features: Quassel_Features) {
    IntSerializer.serialize(buffer, data.size, features)
    data.entries.forEach { (key, value) ->
      StringSerializer.UTF16.serialize(buffer, key, features)
      VariantSerializer.serialize(buffer, value, features)
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): QVariantMap {
    return mutableMapOf(*(0 until IntSerializer.deserialize(buffer, features)).map {
      Pair(
        StringSerializer.UTF16.deserialize(buffer, features) ?: "",
        VariantSerializer.deserialize(buffer, features)
      )
    }.toTypedArray())
  }

}
