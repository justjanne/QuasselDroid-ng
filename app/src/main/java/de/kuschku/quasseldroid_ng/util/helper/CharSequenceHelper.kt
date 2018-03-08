package de.kuschku.quasseldroid_ng.util.helper

fun CharSequence.lastWord(cursor: Int = this.length,
                          onlyBeforeCursor: Boolean = false): CharSequence {
  return lastWordIndices(cursor, onlyBeforeCursor)?.let { subSequence(it) } ?: ""
}

fun CharSequence.lastWordIndices(cursor: Int = this.length,
                                 onlyBeforeCursor: Boolean = false): IntRange? {
  val cursorPosition = if (cursor != -1) {
    cursor
  } else {
    length
  }

  val beginningOfWord = lastIndexOf(' ', cursorPosition - 1)
  val endOfWord = indexOf(' ', cursorPosition)

  val start = beginningOfWord + 1
  val end = when {
    onlyBeforeCursor -> cursorPosition
    endOfWord == -1  -> cursorPosition
    else             -> endOfWord
  }

  return if (end - start > 0 && start >= 0 && end <= length) {
    start until end
  } else {
    null
  }
}