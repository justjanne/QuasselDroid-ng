package de.kuschku.quasseldroid_ng.util.helper

import android.text.Selection
import android.widget.EditText

val CharSequence.selection: IntRange
  get() = Selection.getSelectionStart(this) until Selection.getSelectionEnd(this)

val EditText.selection: IntRange
  get() = text.selection