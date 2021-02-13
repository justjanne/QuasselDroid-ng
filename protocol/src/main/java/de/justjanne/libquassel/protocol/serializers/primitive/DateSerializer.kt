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

package de.justjanne.libquassel.protocol.serializers.primitive

import de.justjanne.libquassel.protocol.features.FeatureSet
import de.justjanne.libquassel.protocol.io.ChainedByteBuffer
import de.justjanne.libquassel.protocol.variant.QtType
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.JulianFields
import java.nio.ByteBuffer

object DateSerializer : QtSerializer<LocalDate> {
  override val qtType: QtType = QtType.QDate
  override val javaType: Class<out LocalDate> = LocalDate::class.java

  override fun serialize(buffer: ChainedByteBuffer, data: LocalDate, featureSet: FeatureSet) {
    val julianDay = data.getLong(JulianFields.JULIAN_DAY).toInt()
    IntSerializer.serialize(buffer, julianDay, featureSet)
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet): LocalDate {
    val julianDay = IntSerializer.deserialize(buffer, featureSet).toLong()
    return LocalDate.ofEpochDay(0).with(JulianFields.JULIAN_DAY, julianDay)
  }
}
