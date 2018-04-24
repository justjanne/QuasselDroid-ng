/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object ByteArraySerializer : Serializer<ByteBuffer?> {
  override fun serialize(buffer: ChainedByteBuffer, data: ByteBuffer?, features: QuasselFeatures) {
    if (data == null) {
      IntSerializer.serialize(buffer, -1, features)
    } else {
      IntSerializer.serialize(buffer, data.remaining(), features)
      buffer.put(data)
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): ByteBuffer? {
    val len = IntSerializer.deserialize(buffer, features)
    return if (len == -1) {
      null
    } else {
      val result = ByteBuffer.allocate(len)
      while (result.hasRemaining())
        result.put(buffer.get())
      result.flip()
      result
    }
  }
}
