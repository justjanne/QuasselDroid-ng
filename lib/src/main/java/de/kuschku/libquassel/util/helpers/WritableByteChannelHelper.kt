package de.kuschku.libquassel.util.helpers

import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.channels.WritableByteChannel

fun WritableByteChannel.write(buffer: ChainedByteBuffer) {
  buffer.write(this)
  buffer.clear()
}
