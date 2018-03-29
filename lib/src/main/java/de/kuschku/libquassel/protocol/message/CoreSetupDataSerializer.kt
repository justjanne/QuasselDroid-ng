package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value

object CoreSetupDataSerializer : HandshakeMessageSerializer<HandshakeMessage.CoreSetupData> {
  override fun serialize(data: HandshakeMessage.CoreSetupData) = mapOf(
    "MsgType" to QVariant.of("CoreSetupData", Type.QString),
    "SetupData" to QVariant.of(mapOf(
      "AdminUser" to QVariant.of(data.adminUser, Type.QString),
      "AdminPasswd" to QVariant.of(data.adminPassword, Type.QString),
      "Backend" to QVariant.of(data.backend, Type.QString),
      "ConnectionProperties" to QVariant.of(data.setupData, Type.QVariantMap),
      "Authenticator" to QVariant.of(data.authenticator, Type.QString),
      "AuthProperties" to QVariant.of(data.authSetupData, Type.QVariantMap)
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
