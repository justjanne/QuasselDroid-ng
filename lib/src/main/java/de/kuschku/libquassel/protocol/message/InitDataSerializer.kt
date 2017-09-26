package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.util.helpers.deserializeString
import de.kuschku.libquassel.util.helpers.serializeString
import java.nio.ByteBuffer

object InitDataSerializer : SignalProxyMessageSerializer<SignalProxyMessage.InitData> {
  override fun serialize(data: SignalProxyMessage.InitData) = listOf(
    QVariant_(RequestType.InitData.value, Type.Int),
    QVariant_(data.className.serializeString(StringSerializer.UTF8), Type.QByteArray),
    QVariant_(data.objectName.serializeString(StringSerializer.UTF8), Type.QByteArray),
    QVariant_(data.initData, Type.QVariantMap)
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.InitData(
    data[0].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
    data[1].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
    data.drop(2).toVariantMap()
  )
}
