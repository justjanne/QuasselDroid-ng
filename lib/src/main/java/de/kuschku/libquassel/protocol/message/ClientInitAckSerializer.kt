package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.util.Flags

object ClientInitAckSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientInitAck> {
  override fun serialize(data: HandshakeMessage.ClientInitAck) = mapOf(
    "MsgType" to QVariant_("ClientInitAck", Type.QString),
    "CoreFeatures" to QVariant_(data.coreFeatures?.toInt(), Type.UInt),
    "StorageBackends" to QVariant_(data.backendInfo, Type.QVariantList),
    "Authenticator" to QVariant_(data.authenticatorInfo, Type.QVariantList),
    "Configured" to QVariant_(data.coreConfigured, Type.Bool),
    "FeatureList" to QVariant_(data.featureList, Type.QStringList)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientInitAck(
    coreFeatures = Flags.Companion.of(data["CoreFeatures"].value(0)),
    backendInfo = data["StorageBackends"].value(),
    authenticatorInfo = data["Authenticators"].value(),
    coreConfigured = data["Configured"].value(),
    featureList = data["FeatureList"].value(emptyList())
  )
}
