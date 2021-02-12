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

package info.quasseldroid.protocol.serializers.primitive

import info.quasseldroid.protocol.features.FeatureSet
import info.quasseldroid.protocol.io.ChainedByteBuffer
import info.quasseldroid.protocol.variant.QtType
import org.threeten.bp.LocalTime
import java.nio.ByteBuffer

object TimeSerializer : QtSerializer<LocalTime> {
  override val qtType: QtType = QtType.QTime
  override val javaType: Class<out LocalTime> = LocalTime::class.java

  override fun serialize(buffer: ChainedByteBuffer, data: LocalTime, featureSet: FeatureSet) {
    val millisecondOfDay = (data.toNanoOfDay() / 1_000_000).toInt()
    IntSerializer.serialize(buffer, millisecondOfDay, featureSet)
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet): LocalTime {
    val millisecondOfDay = IntSerializer.deserialize(buffer, featureSet).toLong()
    return LocalTime.ofNanoOfDay(millisecondOfDay * 1_000_000)
  }
}
