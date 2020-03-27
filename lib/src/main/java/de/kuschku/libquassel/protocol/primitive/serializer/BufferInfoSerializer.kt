/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object BufferInfoSerializer : Serializer<BufferInfo> {
  override fun serialize(buffer: ChainedByteBuffer, data: BufferInfo, features: QuasselFeatures) {
    BufferIdSerializer.serialize(buffer, data.bufferId, features)
    NetworkIdSerializer.serialize(buffer, data.networkId, features)
    ShortSerializer.serialize(buffer, data.type.toShort(), features)
    IntSerializer.serialize(buffer, data.groupId, features)
    StringSerializer.UTF8.serialize(buffer, data.bufferName, features)
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): BufferInfo {
    val bufferId = BufferIdSerializer.deserialize(buffer, features)
    val networkId = NetworkIdSerializer.deserialize(buffer, features)
    val type = Buffer_Type.of(ShortSerializer.deserialize(buffer, features))
    val groupId = IntSerializer.deserialize(buffer, features)
    val bufferName = StringSerializer.UTF8.deserialize(buffer, features)
    return BufferInfo(
      bufferId = bufferId,
      networkId = networkId,
      type = type,
      groupId = groupId,
      bufferName = bufferName
    )
  }
}
