package de.kuschku.quasseldroid.util.helper

import android.text.Spanned

inline fun <reified U> Spanned.spans(range: IntRange) =
  getSpans(range.start, range.endInclusive + 1, U::class.java).filter {
    getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
    (getSpanEnd(it) != range.start ||
     getSpanFlags(it) and 0x02 != 0)
  }

inline fun <reified U> Spanned.spans(range: IntRange, f: (U) -> Boolean) =
  getSpans(range.start, range.last + 1, U::class.java).filter {
    f(it) &&
    getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
    (getSpanEnd(it) != range.start ||
     getSpanFlags(it) and 0x02 != 0)
  }

inline fun <reified U> Spanned.hasSpans(range: IntRange) =
  getSpans(range.start, range.endInclusive + 1, U::class.java).any {
    getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
    (getSpanEnd(it) != range.start ||
     getSpanFlags(it) and 0x02 != 0)
  }

inline fun <reified U> Spanned.hasSpans(range: IntRange, f: (U) -> Boolean) =
  getSpans(range.start, range.last + 1, U::class.java).any {
    f(it) &&
    getSpanFlags(it) and Spanned.SPAN_COMPOSING == 0 &&
    (getSpanEnd(it) != range.start ||
     getSpanFlags(it) and 0x02 != 0)
  }
