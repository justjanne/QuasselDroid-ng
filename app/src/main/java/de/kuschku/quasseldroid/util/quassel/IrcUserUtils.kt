package de.kuschku.quasseldroid.util.quassel

import java.util.*

object IrcUserUtils {
  fun senderColor(nick: String): Int {
    return 0xf and CRCUtils.qChecksum(
      nick.trimEnd('_').toLowerCase(Locale.US).toByteArray(Charsets.ISO_8859_1)
    )
  }

  fun nick(hostmask: String): String {
    return hostmask.substring(
      0,
      hostmask.lastIndex('!', hostmask.lastIndex('@')) ?: hostmask.length
    )
  }

  fun user(hostmask: String): String {
    return hostmask.substring(
      (hostmask.lastIndex('!', hostmask.lastIndex('@')) ?: -1) + 1,
      hostmask.lastIndex('@') ?: hostmask.length
    )
  }

  fun host(hostmask: String): String {
    return hostmask.substring(
      (hostmask.lastIndex('@') ?: -1) + 1
    )
  }

  fun mask(hostmask: String): String {
    return hostmask.substring(
      (hostmask.lastIndex('!', hostmask.lastIndex('@')) ?: -1) + 1
    )
  }

  private fun String.firstIndex(char: Char,
                                startIndex: Int? = null,
                                ignoreCase: Boolean = false): Int? {
    val lastIndex = indexOf(char, startIndex ?: 0, ignoreCase)
    if (lastIndex < 0)
      return null
    else
      return lastIndex
  }

  private fun String.lastIndex(char: Char,
                               startIndex: Int? = null,
                               ignoreCase: Boolean = false): Int? {
    val lastIndex = lastIndexOf(char, startIndex ?: lastIndex, ignoreCase)
    if (lastIndex < 0)
      return null
    else
      return lastIndex
  }
}