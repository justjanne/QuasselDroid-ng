/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.quasseldroid.persistence

import androidx.room.TypeConverter
import de.kuschku.libquassel.protocol.*
import org.threeten.bp.Instant

class MessageTypeConverter {
  @TypeConverter
  fun convertInstant(value: Long): Instant = Instant.ofEpochMilli(value)

  @TypeConverter
  fun convertInstant(value: Instant) = value.toEpochMilli()

  @TypeConverter
  fun convertBufferTypes(value: Buffer_Types) = value.toShort()

  @TypeConverter
  fun convertBufferTypes(value: Short) = Buffer_Type.of(value)

  @TypeConverter
  fun convertMessageTypes(value: Message_Types) = value.toInt()

  @TypeConverter
  fun convertMessageTypes(value: Int) = Message.MessageType.of(value)

  @TypeConverter
  fun convertMessageFlags(value: Message_Flags) = value.toInt()

  @TypeConverter
  fun convertMessageFlags(value: Int) = Message.MessageFlag.of(value)
}
