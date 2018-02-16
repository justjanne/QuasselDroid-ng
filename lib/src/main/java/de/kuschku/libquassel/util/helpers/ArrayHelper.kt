package de.kuschku.libquassel.util.helpers

import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.WARN
import de.kuschku.libquassel.util.compatibility.log

fun ByteArray.hexDump() {
  for (i in 0 until this.size step 33) {
    log(
      WARN, "HexDump",
      (0 until 33).map { it + i }.filter { it < this.size }.joinToString(" ") {
        String.format("%02x", this[it])
      }
    )
  }
}
