package de.kuschku.quasseldroid.util.ui

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.EditText

class DoubleClickHelper(editText: EditText) : View.OnTouchListener {
  var doubleClickListener: (() -> Unit)? = null

  private val gestureDetector = GestureDetector(
    editText.context, object : GestureDetector.SimpleOnGestureListener() {
    override fun onDoubleTap(e: MotionEvent?): Boolean {
      doubleClickListener?.invoke()
      return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
      return true
    }
  })

  override fun onTouch(v: View?, event: MotionEvent?) = gestureDetector.onTouchEvent(event)
}
