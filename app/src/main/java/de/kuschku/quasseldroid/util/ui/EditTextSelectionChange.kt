package de.kuschku.quasseldroid.util.ui

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet

open class EditTextSelectionChange : AppCompatEditText {
  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr)

  private var selectionChangeListener: ((IntRange) -> Unit)? = null

  fun setSelectionChangeListener(f: (IntRange) -> Unit) {
    selectionChangeListener = f
  }

  fun removeSelectionChangeListener() {
    selectionChangeListener = null
  }

  override fun onSelectionChanged(selStart: Int, selEnd: Int) {
    super.onSelectionChanged(selStart, selEnd)
    selectionChangeListener?.invoke(selStart until selEnd)
  }
}
