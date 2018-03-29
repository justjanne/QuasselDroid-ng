package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value
import org.threeten.bp.Instant

object HeartBeatReplySerializer : SignalProxyMessageSerializer<SignalProxyMessage.HeartBeatReply> {
  override fun serialize(data: SignalProxyMessage.HeartBeatReply) = listOf(
    QVariant.of(RequestType.HeartBeatReply.value, Type.Int),
    QVariant.of(data.timestamp, Type.QDateTime)
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.HeartBeatReply(
    data[0].value(Instant.EPOCH)
  )
}
