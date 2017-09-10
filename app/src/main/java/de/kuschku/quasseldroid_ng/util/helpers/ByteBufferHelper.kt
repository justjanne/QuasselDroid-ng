package de.kuschku.quasseldroid_ng.util.helpers

import java.nio.ByteBuffer

fun ByteBuffer.copyTo(target: ByteBuffer) {
  while (target.remaining() > 8)
    target.putLong(this.long)
  while (target.hasRemaining())
    target.put(this.get())
}
