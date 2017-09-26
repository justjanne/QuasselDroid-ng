package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.util.helpers.deserializeString
import de.kuschku.libquassel.util.helpers.serializeString
import java.nio.ByteBuffer

object SyncMessageSerializer : SignalProxyMessageSerializer<SignalProxyMessage.SyncMessage> {
  override fun serialize(data: SignalProxyMessage.SyncMessage): QVariantList = listOf(
    QVariant_(RequestType.Sync.value, Type.Int),
    QVariant_(data.className.serializeString(StringSerializer.UTF8), Type.QByteArray),
    QVariant_(data.objectName.serializeString(StringSerializer.UTF8), Type.QByteArray),
    QVariant_(data.slotName.serializeString(StringSerializer.UTF8), Type.QByteArray),
    *data.params.toTypedArray()
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.SyncMessage(
    data[0].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
    data[1].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
    data[2].value<ByteBuffer?>().deserializeString(StringSerializer.UTF8) ?: "",
    data.drop(3)
  )
}
