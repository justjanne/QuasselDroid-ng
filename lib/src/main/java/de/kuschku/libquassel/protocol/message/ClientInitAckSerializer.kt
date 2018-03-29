package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.util.Flags

object ClientInitAckSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientInitAck> {
  override fun serialize(data: HandshakeMessage.ClientInitAck) = mapOf(
    "MsgType" to QVariant.of<All_>("ClientInitAck", Type.QString),
    "CoreFeatures" to QVariant.of<All_>(data.coreFeatures?.toInt(), Type.UInt),
    "StorageBackends" to QVariant.of<All_>(data.backendInfo, Type.QVariantList),
    "Authenticator" to QVariant.of<All_>(data.authenticatorInfo, Type.QVariantList),
    "Configured" to QVariant.of<All_>(data.coreConfigured, Type.Bool),
    "FeatureList" to QVariant.of<All_>(data.featureList, Type.QStringList)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientInitAck(
    coreFeatures = Flags.Companion.of(data["CoreFeatures"].value(0)),
    backendInfo = data["StorageBackends"].value(),
    authenticatorInfo = data["Authenticators"].value(),
    coreConfigured = data["Configured"].value(),
    featureList = data["FeatureList"].value(emptyList())
  )
}
