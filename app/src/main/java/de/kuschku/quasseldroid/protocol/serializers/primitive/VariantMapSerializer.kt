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

package de.kuschku.quasseldroid.protocol.serializers.primitive

import de.kuschku.quasseldroid.protocol.io.ChainedByteBuffer
import de.kuschku.quasseldroid.protocol.serializers.QtSerializer
import de.kuschku.quasseldroid.protocol.variant.QVariantMap
import de.kuschku.quasseldroid.protocol.variant.QVariant_
import de.kuschku.quasseldroid.protocol.variant.QtType
import java.nio.ByteBuffer

object VariantMapSerializer : QtSerializer<QVariantMap> {
  override val qtType = QtType.QVariantMap
  @Suppress("UNCHECKED_CAST")
  override val javaType: Class<out QVariantMap> = Map::class.java as Class<QVariantMap>

  override fun serialize(buffer: ChainedByteBuffer, data: QVariantMap) {
    IntSerializer.serialize(buffer, data.size)
    data.entries.forEach { (key, value) ->
      StringSerializerUtf16.serialize(buffer, key)
      VariantSerializer.serialize(buffer, value)
    }
  }

  override fun deserialize(buffer: ByteBuffer): QVariantMap {
    val result = mutableMapOf<String, QVariant_>()
    val length = IntSerializer.deserialize(buffer)
    for (i in 0 until length) {
      result[StringSerializerUtf16.deserialize(buffer) ?: ""] = VariantSerializer.deserialize(buffer)
    }
    return result
  }
}
