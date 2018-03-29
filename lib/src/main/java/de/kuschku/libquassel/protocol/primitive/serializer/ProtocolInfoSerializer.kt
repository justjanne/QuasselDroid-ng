package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Protocol_Features
import de.kuschku.libquassel.quassel.ProtocolInfo
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object ProtocolInfoSerializer : Serializer<ProtocolInfo> {
  override fun serialize(buffer: ChainedByteBuffer, data: ProtocolInfo,
                         features: QuasselFeatures) {
    ByteSerializer.serialize(buffer, data.flags.toByte(), features)
    ShortSerializer.serialize(buffer, data.data, features)
    ByteSerializer.serialize(buffer, data.version, features)
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): ProtocolInfo {
    return ProtocolInfo(
      Protocol_Features.of(ByteSerializer.deserialize(buffer, features).toInt()),
      ShortSerializer.deserialize(buffer, features),
      ByteSerializer.deserialize(buffer, features)
    )
  }
}
