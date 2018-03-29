package de.kuschku.quasseldroid.util.helper

import clamp

infix fun IntRange.without(other: IntRange): Iterable<IntRange> {
  val otherStart = minOf(other.start, other.last + 1).clamp(this.start, this.last + 1)
  val otherLast = maxOf(other.start, other.last + 1).clamp(this.start, this.last + 1)

  val startingFragment: IntRange = this.start until otherStart
  val endingFragment: IntRange = otherLast + 1 until this.last + 1

  return listOf(startingFragment, endingFragment).filter { it.last >= it.start }
}