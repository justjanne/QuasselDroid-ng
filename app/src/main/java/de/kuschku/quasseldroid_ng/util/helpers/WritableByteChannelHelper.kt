package de.kuschku.quasseldroid_ng.util.helpers

import de.kuschku.quasseldroid_ng.util.nio.ChainedByteBuffer
import java.nio.channels.WritableByteChannel

fun WritableByteChannel.write(buffer: ChainedByteBuffer) {
  buffer.write(this)
  buffer.clear()
}
