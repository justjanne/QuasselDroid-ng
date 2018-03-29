package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.util.helpers.deserializeString
import de.kuschku.libquassel.util.helpers.serializeString
import java.nio.ByteBuffer

object InitRequestSerializer : SignalProxyMessageSerializer<SignalProxyMessage.InitRequest> {
  override fun serialize(data: SignalProxyMessage.InitRequest) = listOf(
    QVariant.of(RequestType.InitRequest.value, Type.Int),
    QVariant.of(data.className.serializeString(StringSerializer.UTF8), Type.QByteArray),
    QVariant.of(data.objectName.serializeString(StringSerializer.UTF8), Type.QByteArray)
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.InitRequest(
    data[0].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
    data[1].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: ""
  )
}
