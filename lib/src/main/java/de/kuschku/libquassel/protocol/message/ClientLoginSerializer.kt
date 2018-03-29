package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value

object ClientLoginSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientLogin> {
  override fun serialize(data: HandshakeMessage.ClientLogin) = mapOf(
    "MsgType" to QVariant.of("ClientLogin", Type.QString),
    "User" to QVariant.of(data.user, Type.QString),
    "Password" to QVariant.of(data.password, Type.QString)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientLogin(
    user = data["User"].value(),
    password = data["Password"].value()
  )
}
