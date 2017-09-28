package de.kuschku.quasseldroid_ng.util

import android.text.Editable
import android.text.TextWatcher

abstract class TextValidator(private val errorListener: (String?) -> Unit,
                             private val error: String) : TextWatcher {
  override fun afterTextChanged(p0: Editable) {
    isValid = validate(p0)
    errorListener(if (isValid) null else error)
    onChanged()
  }

  protected open fun onChanged() = Unit

  override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
  override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

  abstract fun validate(text: Editable): Boolean
  var isValid = false
    private set
}
