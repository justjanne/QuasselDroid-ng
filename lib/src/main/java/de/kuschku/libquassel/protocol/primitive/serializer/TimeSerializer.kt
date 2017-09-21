package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import org.threeten.bp.LocalTime
import java.nio.ByteBuffer

object TimeSerializer : Serializer<LocalTime> {
  override fun serialize(buffer: ChainedByteBuffer, data: LocalTime, features: Quassel_Features) {
    IntSerializer.serialize(buffer, (data.toNanoOfDay() / 1000).toInt(), features)
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): LocalTime {
    return LocalTime.ofNanoOfDay(IntSerializer.deserialize(buffer, features).toLong() * 1000)
  }
}
