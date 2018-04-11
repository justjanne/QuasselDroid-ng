package de.kuschku.quasseldroid.util.helper

inline fun <T> T.letIf(condition: Boolean, f: (T) -> T) = if (condition) f(this) else this
