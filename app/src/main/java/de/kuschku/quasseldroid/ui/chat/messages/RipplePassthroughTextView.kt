package de.kuschku.quasseldroid.ui.chat.messages

import android.content.Context
import android.text.Spannable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView

class RipplePassthroughTextView : TextView {
  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr)

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    super.onTouchEvent(event)
    return movementMethod?.onTouchEvent(this, text as? Spannable, event) == true
  }
}