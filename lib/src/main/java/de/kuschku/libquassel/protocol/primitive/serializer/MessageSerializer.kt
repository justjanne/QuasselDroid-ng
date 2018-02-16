package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.quassel.QuasselFeature
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import org.threeten.bp.Instant
import java.nio.ByteBuffer

object MessageSerializer : Serializer<Message> {
  override fun serialize(buffer: ChainedByteBuffer, data: Message,
                         features: Quassel_Features) {
    IntSerializer.serialize(buffer, data.messageId, features)
    IntSerializer.serialize(buffer, data.time.epochSecond.toInt(), features)
    IntSerializer.serialize(buffer, data.type.toInt(), features)
    ByteSerializer.serialize(buffer, data.flag.toByte(), features)
    BufferInfoSerializer.serialize(buffer, data.bufferInfo, features)
    StringSerializer.UTF8.serialize(buffer, data.sender, features)
    if (features.hasFlag(QuasselFeature.SenderPrefixes))
      StringSerializer.UTF8.serialize(buffer, data.senderPrefixes, features)
    StringSerializer.UTF8.serialize(buffer, data.content, features)
  }

  override fun deserialize(buffer: ByteBuffer,
                           features: Quassel_Features): Message {
    return Message(
      messageId = IntSerializer.deserialize(buffer, features),
      time = Instant.ofEpochSecond(IntSerializer.deserialize(buffer, features).toLong()),
      type = Message.MessageType.of(IntSerializer.deserialize(buffer, features)),
      flag = Message.MessageFlag.of(
        ByteSerializer.deserialize(buffer, features).toInt()
      ),
      bufferInfo = BufferInfoSerializer.deserialize(buffer, features),
      sender = StringSerializer.UTF8.deserialize(buffer, features) ?: "",
      senderPrefixes = if (features.hasFlag(QuasselFeature.SenderPrefixes))
        StringSerializer.UTF8.deserialize(buffer, features) ?: "" else "",
      content = StringSerializer.UTF8.deserialize(buffer, features) ?: ""
    )
  }
}
