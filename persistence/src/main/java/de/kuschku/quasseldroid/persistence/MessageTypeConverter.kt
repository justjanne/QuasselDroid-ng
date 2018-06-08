package de.kuschku.quasseldroid.persistence

import android.arch.persistence.room.TypeConverter
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
