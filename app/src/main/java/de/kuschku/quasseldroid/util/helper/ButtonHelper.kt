package de.kuschku.quasseldroid.util.helper

import android.support.v4.graphics.drawable.DrawableCompat
import android.widget.Button

fun Button.retint() {
  val (left, top, right, bottom) = compoundDrawables.map {
    it?.apply {
      it.mutate()
      DrawableCompat.setTint(it, textColors.defaultColor)
    }
  }
  setCompoundDrawables(left, top, right, bottom)
}
