package de.justjanne.quasseldroid.util

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.text.input.TextFieldValue

object TextFieldValueSaver : Saver<TextFieldValue, String> {
  override fun restore(value: String) = TextFieldValue(value)
  override fun SaverScope.save(value: TextFieldValue) = value.text
}
