package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value

object ClientLoginSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientLogin> {
  override fun serialize(data: HandshakeMessage.ClientLogin) = mapOf(
    "MsgType" to QVariant_("ClientLogin", Type.QString),
    "User" to QVariant_(data.user, Type.QString),
    "Password" to QVariant_(data.password, Type.QString)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientLogin(
    user = data["User"].value(),
    password = data["Password"].value()
  )
}
