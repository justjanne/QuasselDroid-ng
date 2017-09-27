package de.kuschku.libquassel.util.helpers

import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import java.nio.ByteBuffer

fun ByteBuffer.copyTo(target: ByteBuffer) {
  while (target.remaining() > 8)
    target.putLong(this.long)
  while (target.hasRemaining())
    target.put(this.get())
}

fun ByteBuffer?.deserializeString(serializer: StringSerializer) = if (this == null) {
  null
} else {
  serializer.deserializeAll(this)
}

fun ByteBuffer.hexDump() {
  val target = ByteBuffer.allocate(this.capacity())
  this.clear()
  this.copyTo(target)
  target.array().hexDump()
}
