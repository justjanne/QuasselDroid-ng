package de.kuschku.quasseldroid.util.helper

inline fun <R> Boolean.letIf(block: () -> R): R? {
  return if (this) block() else null
}

inline fun <R> Boolean.letUnless(block: () -> R): R? {
  return if (this) null else block()
}
