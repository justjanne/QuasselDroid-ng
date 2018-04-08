package de.kuschku.quasseldroid.util.helper

import android.text.Editable
import android.text.Spanned
import de.kuschku.quasseldroid.util.irc.format.spans.Copyable

inline fun <reified U, T> Editable.removeSpans(
  range: IntRange, removeInvalid: Boolean = false, f: (U) -> T?
) where T : Copyable<T> {
  for (raw in getSpans<U>(range.start, range.endInclusive + 1, U::class.java)) {
    val spanFlags = getSpanFlags(raw)
    if (spanFlags and Spanned.SPAN_COMPOSING != 0) continue

    val spanEnd = getSpanEnd(raw)
    val spanStart = getSpanStart(raw)

    val span = f(raw)
    if (span == null) {
      if (removeInvalid)
        removeSpan(raw)
    } else {
      removeSpan(raw)

      for (spanRange in spanStart until spanEnd without range) {
        setSpan(
          span.copy(),
          spanRange.start,
          spanRange.endInclusive + 1,
          (spanFlags and 0x03.inv()) or 0x01
        )
      }
    }
  }
}
