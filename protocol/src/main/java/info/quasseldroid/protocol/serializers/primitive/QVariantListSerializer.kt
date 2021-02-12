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
import info.quasseldroid.protocol.variant.QVariantList
import info.quasseldroid.protocol.variant.QVariant_
import info.quasseldroid.protocol.variant.QtType
import java.nio.ByteBuffer

object QVariantListSerializer : QtSerializer<QVariantList> {
  override val qtType = QtType.QVariantList

  @Suppress("UNCHECKED_CAST")
  override val javaType: Class<QVariantList> = List::class.java as Class<QVariantList>

  override fun serialize(buffer: ChainedByteBuffer, data: QVariantList, featureSet: FeatureSet) {
    IntSerializer.serialize(buffer, data.size, featureSet)
    data.forEach {
      QVariantSerializer.serialize(buffer, it, featureSet)
    }
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet): QVariantList {
    val result = mutableListOf<QVariant_>()
    val length = IntSerializer.deserialize(buffer, featureSet)
    for (i in 0 until length) {
      result.add(QVariantSerializer.deserialize(buffer, featureSet))
    }
    return result
  }
}
