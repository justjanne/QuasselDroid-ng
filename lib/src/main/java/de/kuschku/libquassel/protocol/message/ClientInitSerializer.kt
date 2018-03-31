package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.util.flag.Flags

object ClientInitSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientInit> {
  override fun serialize(data: HandshakeMessage.ClientInit) = mapOf(
    "MsgType" to QVariant.of("ClientInit", Type.QString),
    "ClientVersion" to QVariant.of(data.clientVersion, Type.QString),
    "ClientDate" to QVariant.of(data.buildDate, Type.QString),
    "Features" to QVariant.of(data.clientFeatures?.toInt(), Type.UInt),
    "FeatureList" to QVariant.of(data.featureList, Type.QStringList)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientInit(
    clientVersion = data["ClientVersion"].value(),
    buildDate = data["ClientDate"].value(),
    clientFeatures = Flags.of(data["Features"].value(0)),
    featureList = data["FeatureList"].value(emptyList())
  )
}
