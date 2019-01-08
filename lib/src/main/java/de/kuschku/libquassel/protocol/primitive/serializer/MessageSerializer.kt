/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import org.threeten.bp.Instant
import java.nio.ByteBuffer

object MessageSerializer : Serializer<Message> {
  override fun serialize(buffer: ChainedByteBuffer, data: Message, features: QuasselFeatures) {
    SignedId64Serializer.serialize(buffer, data.messageId, features)
    if (features.hasFeature(ExtendedFeature.LongTime))
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
      messageId = SignedId64Serializer.deserialize(buffer, features),
      time = if (features.hasFeature(ExtendedFeature.LongTime))
        Instant.ofEpochMilli(LongSerializer.deserialize(buffer, features))
      else
        Instant.ofEpochSecond(IntSerializer.deserialize(buffer, features).toLong()),
      type = Message.MessageType.of(IntSerializer.deserialize(buffer, features)),
      flag = Message.MessageFlag.of(
        ByteSerializer.deserialize(buffer, features).toInt() and 0xff
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
