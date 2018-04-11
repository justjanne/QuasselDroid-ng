package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import org.threeten.bp.Instant
import java.nio.ByteBuffer

object MessageSerializer : Serializer<Message> {
  override fun serialize(buffer: ChainedByteBuffer, data: Message, features: QuasselFeatures) {
    IntSerializer.serialize(buffer, data.messageId, features)
    if (features.hasFeature(ExtendedFeature.LongMessageTime))
      LongSerializer.serialize(buffer, data.time.toEpochMilli(), features)
    else
      IntSerializer.serialize(buffer, data.time.epochSecond.toInt(), features)
    IntSerializer.serialize(buffer, data.type.toInt(), features)
    ByteSerializer.serialize(buffer, data.flag.toByte(), features)
    BufferInfoSerializer.serialize(buffer, data.bufferInfo, features)
    StringSerializer.UTF8.serialize(buffer, data.sender, features)
    if (features.hasFeature(ExtendedFeature.SenderPrefixes))
      StringSerializer.UTF8.serialize(buffer, data.senderPrefixes, features)
    if (features.hasFeature(ExtendedFeature.RichMessages))
      StringSerializer.UTF8.serialize(buffer, data.realName, features)
    if (features.hasFeature(ExtendedFeature.RichMessages))
      StringSerializer.UTF8.serialize(buffer, data.avatarUrl, features)
    StringSerializer.UTF8.serialize(buffer, data.content, features)
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): Message {
    return Message(
      messageId = IntSerializer.deserialize(buffer, features),
      time = if (features.hasFeature(ExtendedFeature.LongMessageTime))
        Instant.ofEpochMilli(LongSerializer.deserialize(buffer, features))
      else
        Instant.ofEpochSecond(IntSerializer.deserialize(buffer, features).toLong()),
      type = Message.MessageType.of(IntSerializer.deserialize(buffer, features)),
      flag = Message.MessageFlag.of(
        ByteSerializer.deserialize(buffer, features).toInt()
      ),
      bufferInfo = BufferInfoSerializer.deserialize(buffer, features),
      sender = StringSerializer.UTF8.deserialize(buffer, features) ?: "",
      senderPrefixes = if (features.hasFeature(ExtendedFeature.SenderPrefixes))
        StringSerializer.UTF8.deserialize(buffer, features) ?: "" else "",
      realName = if (features.hasFeature(ExtendedFeature.RichMessages))
        StringSerializer.UTF8.deserialize(buffer, features) ?: "" else "",
      avatarUrl = if (features.hasFeature(ExtendedFeature.RichMessages))
        StringSerializer.UTF8.deserialize(buffer, features) ?: "" else "",
      content = StringSerializer.UTF8.deserialize(buffer, features) ?: ""
    )
  }
}
