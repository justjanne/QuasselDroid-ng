package de.justjanne.quasseldroid.util.extensions

fun <T> List<T>.getSafe(index: Int): T? =
  if (index !in 0..size) null
  else get(index)

fun <T> List<T>.getPrevious(index: Int): T? =
  getSafe(index - 1)

fun <T> List<T>.getNext(index: Int): T? =
  getSafe(index + 1)

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> List<T>.component6(): T = get(5)
@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> List<T>.component7(): T = get(6)
@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> List<T>.component8(): T = get(7)
@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> List<T>.component9(): T = get(8)
@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> List<T>.component10(): T = get(9)
@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> List<T>.component11(): T = get(10)
@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> List<T>.component12(): T = get(11)
