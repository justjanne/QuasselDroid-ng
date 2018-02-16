package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value

object CoreSetupDataSerializer : HandshakeMessageSerializer<HandshakeMessage.CoreSetupData> {
  override fun serialize(data: HandshakeMessage.CoreSetupData) = mapOf(
    "MsgType" to QVariant_("CoreSetupData", Type.QString),
    "SetupData" to QVariant_(
      mapOf(
        "AdminUser" to QVariant_(data.adminUser, Type.QString),
        "AdminPasswd" to QVariant_(data.adminPassword, Type.QString),
        "Backend" to QVariant_(data.backend, Type.QString),
        "ConnectionProperties" to QVariant_(data.setupData, Type.QVariantMap),
        "Authenticator" to QVariant_(data.authenticator, Type.QString),
        "AuthProperties" to QVariant_(data.authSetupData, Type.QVariantMap)
      ), Type.QVariantMap
    )
  )

  override fun deserialize(data: QVariantMap): HandshakeMessage.CoreSetupData {
    val setupData = data["SetupData"].value<QVariantMap?>()
    return HandshakeMessage.CoreSetupData(
      adminUser = setupData?.get("AdminUser").value(),
      adminPassword = setupData?.get("AdminPasswd").value(),
      backend = setupData?.get("Backend").value(),
      setupData = setupData?.get("ConnectionProperties").value(),
      authenticator = setupData?.get("Authenticator").value(),
      authSetupData = setupData?.get("AuthProperties").value()
    )
  }
}
