package de.justjanne.quasseldroid.util.irc

import java.util.*

object SenderColorUtil {
  fun senderColor(nick: String): Int {
    return 0xf and CRCUtils.qChecksum(
      nick.trimEnd('_')
        .lowercase(Locale.ENGLISH)
        .toByteArray(Charsets.ISO_8859_1)
    )
  }
}
