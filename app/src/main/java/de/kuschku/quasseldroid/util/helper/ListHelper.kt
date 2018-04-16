package de.kuschku.quasseldroid.util.helper

inline fun <T, U> List<T>.mapReverse(mapper: (T) -> U): List<U> {
  val result = mutableListOf<U>()
  for (i in size - 1 downTo 0) {
    result.add(0, mapper(this[i]))
  }
  return result
}
