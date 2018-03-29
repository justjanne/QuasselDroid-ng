package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value

object SessionInitSerializer : HandshakeMessageSerializer<HandshakeMessage.SessionInit> {
  override fun serialize(data: HandshakeMessage.SessionInit) = mapOf(
    "MsgType" to QVariant.of("SessionInit", Type.QString),
    "SessionState" to QVariant.of(mapOf(
      "BufferInfos" to QVariant.of(data.bufferInfos, Type.QVariantList),
      "NetworkIds" to QVariant.of(data.networkIds, Type.QVariantList),
      "Identities" to QVariant.of(data.identities, Type.QVariantList)
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
