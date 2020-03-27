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

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.value
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object HandshakeVariantMapSerializer : Serializer<QVariantMap> {
  override fun serialize(buffer: ChainedByteBuffer, data: QVariantMap, features: QuasselFeatures) {
    IntSerializer.serialize(buffer, data.size * 2, features)
    data.entries.forEach { (key, value) ->
      VariantSerializer.serialize(buffer, QVariant.of(key, Type.QString), features)
      VariantSerializer.serialize(buffer, value, features)
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): QVariantMap {
    val range = 0 until IntSerializer.deserialize(buffer, features) / 2
    val pairs = range.map {
      val keyRaw: ByteBuffer? = VariantSerializer.deserialize(buffer, features).value()
      val key: String? = if (keyRaw != null) {
        StringSerializer.UTF8.deserializeAll(keyRaw)
      } else {
        null
      }
      val value = VariantSerializer.deserialize(buffer, features)
      Pair(key ?: "", value)
    }
    val pairArray = pairs.toTypedArray()
    return mapOf(*pairArray)
  }
}
