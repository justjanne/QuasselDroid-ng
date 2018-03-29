package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object VariantSerializer : Serializer<QVariant_> {
  override fun serialize(buffer: ChainedByteBuffer, data: QVariant_, features: QuasselFeatures) {
    IntSerializer.serialize(buffer, data.type.id, features)
    BoolSerializer.serialize(buffer, false, features)
    if (data is QVariant.Custom && data.type == Type.UserType) {
      StringSerializer.C.serialize(buffer, data.qtype.name, features)
    }
    (data.serializer as Serializer<Any?>).serialize(buffer, data.data, features)
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): QVariant_ {
    val rawType = IntSerializer.deserialize(buffer, features)
    val type = Type.of(rawType)
    val isNull = BoolSerializer.deserialize(buffer, features)

    return if (type == Type.UserType) {
      val name = StringSerializer.C.deserialize(buffer, features)
      val qType = name?.let(QType.Companion::of)
                  ?: throw IllegalArgumentException("No such type: $name")
      val value = qType.serializer.deserialize(buffer, features)
      QVariant.of<All_>(value, qType)
    } else {
      val serializer = type?.serializer ?: throw IllegalArgumentException("No such type: $type")
      val value = serializer.deserialize(buffer, features)
      QVariant.of<All_>(value, type)
    }
  }
}
