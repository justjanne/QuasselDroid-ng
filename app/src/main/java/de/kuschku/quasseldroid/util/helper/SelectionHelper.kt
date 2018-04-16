package de.kuschku.quasseldroid.util.helper

import android.text.Selection
import android.widget.EditText

val CharSequence.selection: IntRange
  get() {
    val start = Selection.getSelectionStart(this)
    val end = Selection.getSelectionEnd(this)

    return minOf(start, end) until maxOf(start, end)
  }

val EditText.selection: IntRange
  get() = text.selection
