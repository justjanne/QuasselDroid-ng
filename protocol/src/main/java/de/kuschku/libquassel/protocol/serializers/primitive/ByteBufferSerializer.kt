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

package de.kuschku.libquassel.protocol.serializers.primitive

import de.kuschku.libquassel.protocol.features.FeatureSet
import de.kuschku.libquassel.protocol.io.ChainedByteBuffer
import de.kuschku.libquassel.protocol.io.copyData
import de.kuschku.libquassel.protocol.io.print
import de.kuschku.libquassel.protocol.variant.QtType
import java.nio.ByteBuffer

object ByteBufferSerializer : QtSerializer<ByteBuffer?> {
  override val qtType: QtType = QtType.QByteArray
  override val javaType: Class<out ByteBuffer?> = ByteBuffer::class.java

  override fun serialize(buffer: ChainedByteBuffer, data: ByteBuffer?, featureSet: FeatureSet) {
    IntSerializer.serialize(buffer, data?.remaining() ?: 0, featureSet)
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
