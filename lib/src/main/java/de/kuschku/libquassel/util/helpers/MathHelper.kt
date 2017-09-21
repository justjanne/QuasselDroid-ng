inline fun Int.clamp(lowerBound: Int, upperBound: Int): Int
  = maxOf(lowerBound, minOf(this, upperBound))
