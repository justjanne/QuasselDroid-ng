package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.util.Flags

object ClientInitSerializer : HandshakeMessageSerializer<HandshakeMessage.ClientInit> {
  override fun serialize(data: HandshakeMessage.ClientInit) = mapOf(
    "MsgType" to QVariant_("ClientInit", Type.QString),
    "ClientVersion" to QVariant_(data.clientVersion, Type.QString),
    "ClientDate" to QVariant_(data.buildDate, Type.QString),
    "Features" to QVariant_(data.clientFeatures?.toInt(), Type.UInt)
  )

  override fun deserialize(data: QVariantMap) = HandshakeMessage.ClientInit(
    clientVersion = data["ClientVersion"].value(),
    buildDate = data["ClientDate"].value(),
    clientFeatures = Flags.Companion.of(data["Features"].value(0))
  )
}
