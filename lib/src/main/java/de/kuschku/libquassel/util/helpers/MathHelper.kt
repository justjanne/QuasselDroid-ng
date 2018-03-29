inline fun Int.clamp(lowerBound: Int, upperBound: Int): Int =
  maxOf(lowerBound, minOf(this, upperBound))

inline fun Int.clamp(range: IntRange): Int = clamp(range.start, range.last)