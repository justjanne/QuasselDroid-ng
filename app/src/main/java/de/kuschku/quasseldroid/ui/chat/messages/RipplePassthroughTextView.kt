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

  // The goal is to provide all normal interaction to the parent view, unless a link is touched
  // But additionally, we want to provide all normal textview interactions as well
  override fun onTouchEvent(event: MotionEvent): Boolean {
    val movementMethod = this.movementMethod
    this.movementMethod = null
    super.onTouchEvent(event)
    this.movementMethod = movementMethod
    return movementMethod?.onTouchEvent(this, text as? Spannable, event) == true
  }
}
