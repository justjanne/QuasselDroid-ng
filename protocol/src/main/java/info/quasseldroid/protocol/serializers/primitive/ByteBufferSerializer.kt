/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package info.quasseldroid.protocol.serializers.primitive

import info.quasseldroid.protocol.features.FeatureSet
import info.quasseldroid.protocol.io.ChainedByteBuffer
import info.quasseldroid.protocol.io.copyData
import info.quasseldroid.protocol.variant.QtType
import java.nio.ByteBuffer

object ByteBufferSerializer : QtSerializer<ByteBuffer?> {
  override val qtType: QtType = QtType.QByteArray
  override val javaType: Class<out ByteBuffer?> = ByteBuffer::class.java

  override fun serialize(buffer: ChainedByteBuffer, data: ByteBuffer?, featureSet: FeatureSet) {
    IntSerializer.serialize(buffer, data?.remaining() ?: -1, featureSet)
    if (data != null) {
      buffer.put(data)
    }
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet): ByteBuffer? {
    val length = IntSerializer.deserialize(buffer, featureSet)
    if (length < 0) {
      return null
    }
    val result = copyData(buffer, length)
    result.limit(length)
    return result
  }
}
