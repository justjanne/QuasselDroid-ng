package de.kuschku.libquassel.protocol.message

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value
import org.threeten.bp.Instant

object HeartBeatSerializer : SignalProxyMessageSerializer<SignalProxyMessage.HeartBeat> {
  override fun serialize(data: SignalProxyMessage.HeartBeat) = listOf(
    QVariant.of(RequestType.HeartBeat.value, Type.Int),
    QVariant.of(data.timestamp, Type.QDateTime)
  )

  override fun deserialize(data: QVariantList) = SignalProxyMessage.HeartBeat(
    data[0].value(Instant.EPOCH)
  )
}
