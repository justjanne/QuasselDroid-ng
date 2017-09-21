package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object BufferInfoSerializer : Serializer<BufferInfo> {
  override fun serialize(buffer: ChainedByteBuffer, data: BufferInfo, features: Quassel_Features) {
    IntSerializer.serialize(buffer, data.bufferId, features)
    IntSerializer.serialize(buffer, data.networkId, features)
    ShortSerializer.serialize(buffer, data.type.toShort(), features)
    IntSerializer.serialize(buffer, data.groupId, features)
    StringSerializer.UTF8.serialize(buffer, data.bufferName, features)
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): BufferInfo {
    val bufferId = IntSerializer.deserialize(buffer, features)
    val networkId = IntSerializer.deserialize(buffer, features)
    val type = Buffer_Type.of(ShortSerializer.deserialize(buffer, features))
    val groupId = IntSerializer.deserialize(buffer, features)
    val bufferName = StringSerializer.UTF8.deserialize(buffer, features)
    return BufferInfo(
      bufferId = bufferId,
      networkId = networkId,
      type = type,
      groupId = groupId,
      bufferName = bufferName
    )
  }
}
