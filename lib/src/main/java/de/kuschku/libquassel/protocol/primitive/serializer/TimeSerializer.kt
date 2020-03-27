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

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import org.threeten.bp.LocalTime
import java.nio.ByteBuffer

object TimeSerializer : Serializer<LocalTime> {
  override fun serialize(buffer: ChainedByteBuffer, data: LocalTime, features: QuasselFeatures) {
    IntSerializer.serialize(buffer, (data.toNanoOfDay() / 1000).toInt(), features)
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): LocalTime {
    return LocalTime.ofNanoOfDay(IntSerializer.deserialize(buffer, features).toLong() * 1000)
  }
}
