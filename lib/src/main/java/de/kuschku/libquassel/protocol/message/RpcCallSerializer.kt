package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.util.helpers.deserializeString
import de.kuschku.libquassel.util.helpers.serializeString
import java.nio.ByteBuffer

object RpcCallSerializer : SignalProxyMessageSerializer<SignalProxyMessage.RpcCall> {
  override fun serialize(data: SignalProxyMessage.RpcCall) = listOf(
    QVariant_(RequestType.RpcCall.value, Type.Int),
    QVariant_(data.slotName.serializeString(StringSerializer.UTF8), Type.QByteArray),
    *data.params.toTypedArray()
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.RpcCall(
    data[0].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
    data.drop(1)
  )
}
