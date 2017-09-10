package de.kuschku.quasseldroid_ng.protocol.primitive.serializer

import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.protocol.Quassel_Features
import de.kuschku.quasseldroid_ng.quassel.QuasselFeature
import de.kuschku.quasseldroid_ng.util.hasFlag
import de.kuschku.quasseldroid_ng.util.nio.ChainedByteBuffer
import org.threeten.bp.Instant
import java.nio.ByteBuffer

object MessageSerializer : Serializer<QuasselDatabase.RawMessage> {
  override fun serialize(buffer: ChainedByteBuffer, data: QuasselDatabase.RawMessage,
                         features: Quassel_Features) {
    IntSerializer.serialize(buffer, data.messageId, features)
    IntSerializer.serialize(buffer, data.time.epochSecond.toInt(), features)
    IntSerializer.serialize(buffer, data.type, features)
    ByteSerializer.serialize(buffer, data.flag.toByte(), features)
    BufferInfoSerializer.serialize(buffer, data.bufferInfo, features)
    StringSerializer.UTF8.serialize(buffer, data.sender, features)
    if (features.hasFlag(QuasselFeature.SenderPrefixes))
      StringSerializer.UTF8.serialize(buffer, data.senderPrefixes, features)
    StringSerializer.UTF8.serialize(buffer, data.content, features)
  }

  override fun deserialize(buffer: ByteBuffer,
                           features: Quassel_Features): QuasselDatabase.RawMessage {
    return QuasselDatabase.RawMessage(
      messageId = IntSerializer.deserialize(buffer, features),
      time = Instant.ofEpochSecond(IntSerializer.deserialize(buffer, features).toLong()),
      type = QuasselDatabase.Message.MessageType.of(IntSerializer.deserialize(buffer, features)),
      flag = QuasselDatabase.Message.MessageFlag.of(
        ByteSerializer.deserialize(buffer, features).toInt()),
      bufferInfo = BufferInfoSerializer.deserialize(buffer, features),
      sender = StringSerializer.UTF8.deserialize(buffer, features) ?: "",
      senderPrefixes = if (features.hasFlag(QuasselFeature.SenderPrefixes))
        StringSerializer.UTF8.deserialize(buffer, features) ?: "" else "",
      content = StringSerializer.UTF8.deserialize(buffer, features) ?: ""
    )
  }
}
