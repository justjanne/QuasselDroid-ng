package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value

object SessionInitSerializer : HandshakeMessageSerializer<HandshakeMessage.SessionInit> {
  override fun serialize(data: HandshakeMessage.SessionInit) = mapOf(
    "MsgType" to QVariant_("SessionInit", Type.QString),
    "SessionState" to QVariant_(
      mapOf(
        "BufferInfos" to QVariant_(data.bufferInfos, Type.QVariantList),
        "NetworkIds" to QVariant_(data.networkIds, Type.QVariantList),
        "Identities" to QVariant_(data.identities, Type.QVariantList)
      ), Type.QVariantMap
    )
  )

  override fun deserialize(data: QVariantMap): HandshakeMessage.SessionInit {
    val setupData = data["SessionState"].value<QVariantMap?>()
    return HandshakeMessage.SessionInit(
      bufferInfos = setupData?.get("BufferInfos").value(),
      networkIds = setupData?.get("NetworkIds").value(),
      identities = setupData?.get("Identities").value()
    )
  }
}
