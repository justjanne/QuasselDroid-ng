package de.justjanne.quasseldroid.util.extensions

fun <T> Sequence<T>.collapse(callback: (T, T) -> T?) = sequence<T> {
  var prev: T? = null
  for (item in iterator()) {
    if (prev != null) {
      val collapsed = callback(prev, item)
      if (collapsed == null) {
        yield(prev)
        prev = item
      } else {
        prev = collapsed
      }
    } else {
      prev = item
    }
  }
  if (prev != null) {
    yield(prev)
  }
}
