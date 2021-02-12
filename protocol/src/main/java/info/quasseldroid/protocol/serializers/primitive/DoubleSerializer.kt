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
import info.quasseldroid.protocol.variant.QtType
import java.nio.ByteBuffer

object DoubleSerializer : QtSerializer<Double> {
  override val qtType: QtType = QtType.Double
  override val javaType: Class<Double> = Double::class.javaObjectType

  override fun serialize(buffer: ChainedByteBuffer, data: Double, featureSet: FeatureSet) {
    buffer.putDouble(data)
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet): Double {
    return buffer.getDouble()
  }
}
