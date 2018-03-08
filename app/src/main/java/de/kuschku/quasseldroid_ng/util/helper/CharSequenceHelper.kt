package de.kuschku.quasseldroid_ng.util.helper

fun CharSequence.lastWord(cursor: Int = this.length,
                          onlyBeforeCursor: Boolean = false): CharSequence {
  val beginningOfWord = lastIndexOf(' ', cursor)
  val endOfWord = indexOf(' ', cursor)
  val start = beginningOfWord + 1
  val end = if (endOfWord != -1) {
    endOfWord
  } else {
    length
  }

  return subSequence(start, if (onlyBeforeCursor) cursor else end)
}