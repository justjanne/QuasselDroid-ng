package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type

object CoreSetupAckSerializer : HandshakeMessageSerializer<HandshakeMessage.CoreSetupAck> {
  override fun serialize(data: HandshakeMessage.CoreSetupAck) = mapOf(
    "MsgType" to QVariant.of("CoreSetupAck", Type.QString)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.CoreSetupAck()
}
