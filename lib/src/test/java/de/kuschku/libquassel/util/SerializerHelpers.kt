package de.kuschku.libquassel.util

import de.kuschku.libquassel.protocol.primitive.serializer.Serializer
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer

fun <T> roundTrip(serializer: Serializer<T>, value: T,
                  features: QuasselFeatures = QuasselFeatures.all()): T {
  val chainedBuffer = ChainedByteBuffer(
    direct = false
  )
  serializer.serialize(chainedBuffer, value, features)
  val buffer = chainedBuffer.toBuffer()
  return serializer.deserialize(buffer, features)
}
