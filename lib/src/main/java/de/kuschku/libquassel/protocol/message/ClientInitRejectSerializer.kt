package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value

object ClientInitRejectSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientInitReject> {
  override fun serialize(data: HandshakeMessage.ClientInitReject) = mapOf(
    "MsgType" to QVariant.of("ClientInitReject", Type.QString),
    "Error" to QVariant.of(data.errorString, Type.QString)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientInitReject(
    errorString = data["Error"].value()
  )
}
