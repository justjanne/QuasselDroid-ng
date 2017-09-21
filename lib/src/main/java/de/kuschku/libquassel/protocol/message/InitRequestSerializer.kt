package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.protocol.primitive.serializer.deserializeString
import de.kuschku.libquassel.protocol.primitive.serializer.serializeString
import de.kuschku.libquassel.protocol.value
import java.nio.ByteBuffer

object InitRequestSerializer : SignalProxyMessageSerializer<SignalProxyMessage.InitRequest> {
  override fun serialize(data: SignalProxyMessage.InitRequest) = listOf(
    QVariant_(RequestType.InitRequest.value, Type.Int),
    QVariant_(data.className.serializeString(StringSerializer.UTF8), Type.QByteArray),
    QVariant_(data.objectName.serializeString(StringSerializer.UTF8), Type.QByteArray)
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.InitRequest(
    data[0].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
    data[1].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: ""
  )
}
