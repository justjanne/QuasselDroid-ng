package de.kuschku.quasseldroid_ng.util.helper

private class DelimitedRangesSequence(
  private val input: CharSequence,
  private val startIndex: Int,
  private val limit: Int,
  private val getNextMatch: CharSequence.(Int) -> Pair<Int, Int>?
) : Sequence<IntRange> {
  override fun iterator(): Iterator<IntRange> = object : Iterator<IntRange> {
    var nextState: Int = -1 // -1 for unknown, 0 for done, 1 for continue
    var currentStartIndex: Int = startIndex.coerceIn(0, input.length)
    var nextSearchIndex: Int = currentStartIndex
    var nextItem: IntRange? = null
    var counter: Int = 0

    private fun calcNext() {
      if (nextSearchIndex < 0) {
        nextState = 0
        nextItem = null
      } else {
        if (limit > 0 && ++counter >= limit || nextSearchIndex > input.length) {
          nextItem = currentStartIndex..input.lastIndex
          nextSearchIndex = -1
        } else {
          val match = input.getNextMatch(nextSearchIndex)
          if (match == null) {
            nextItem = currentStartIndex..input.lastIndex
            nextSearchIndex = -1
          } else {
            val (index, length) = match
            nextItem = currentStartIndex..index - 1
            currentStartIndex = index + length
            nextSearchIndex = currentStartIndex + if (length == 0) 1 else 0
          }
        }
        nextState = 1
      }
    }

    override fun next(): IntRange {
      if (nextState == -1)
        calcNext()
      if (nextState == 0)
        throw NoSuchElementException()
      val result = nextItem as IntRange
      // Clean next to avoid keeping reference on yielded instance
      nextItem = null
      nextState = -1
      return result
    }

    override fun hasNext(): Boolean {
      if (nextState == -1)
        calcNext()
      return nextState == 1
    }
  }
}

internal fun CharSequence.regionMatchesImpl(thisOffset: Int, other: CharSequence, otherOffset: Int,
                                            length: Int, ignoreCase: Boolean): Boolean {
  if ((otherOffset < 0) || (thisOffset < 0) || (thisOffset > this.length - length)
      || (otherOffset > other.length - length)) {
    return false
  }

  for (index in 0 until length) {
    if (!this[thisOffset + index].equals(other[otherOffset + index], ignoreCase))
      return false
  }
  return true
}

private fun CharSequence.findAnyOf(strings: Collection<String>, startIndex: Int,
                                   ignoreCase: Boolean, last: Boolean): Pair<Int, String>? {
  if (!ignoreCase && strings.size == 1) {
    val string = strings.single()
    val index = if (!last) indexOf(string, startIndex) else lastIndexOf(string, startIndex)
    return if (index < 0) null else index to string
  }

  val indices = if (!last) startIndex.coerceAtLeast(0)..length else startIndex.coerceAtMost(
    lastIndex
  ) downTo 0

  if (this is String) {
    for (index in indices) {
      val matchingString = strings.firstOrNull {
        it.regionMatches(
          0, this, index, it.length, ignoreCase
        )
      }
      if (matchingString != null)
        return index to matchingString
    }
  } else {
    for (index in indices) {
      val matchingString = strings.firstOrNull {
        it.regionMatchesImpl(
          0, this, index, it.length, ignoreCase
        )
      }
      if (matchingString != null)
        return index to matchingString
    }
  }

  return null
}

private fun CharSequence.rangesDelimitedBy(delimiters: Array<out String>, startIndex: Int = 0,
                                           ignoreCase: Boolean = false,
                                           limit: Int = 0): Sequence<IntRange> {
  require(limit >= 0, { "Limit must be non-negative, but was $limit." })
  val delimitersList = delimiters.asList()

  return DelimitedRangesSequence(
    this, startIndex, limit, { startIndex ->
    findAnyOf(
      delimitersList, startIndex, ignoreCase = ignoreCase, last = false
    )?.let { it.first to it.second.length }
  })
}

fun CharSequence.splitToSequence(vararg delimiters: String, ignoreCase: Boolean = false,
                                 limit: Int = 0): Sequence<CharSequence> =
  rangesDelimitedBy(delimiters, ignoreCase = ignoreCase, limit = limit).map { subSequence(it) }

fun CharSequence.lineSequence(): Sequence<CharSequence> = splitToSequence("\r\n", "\n", "\r")