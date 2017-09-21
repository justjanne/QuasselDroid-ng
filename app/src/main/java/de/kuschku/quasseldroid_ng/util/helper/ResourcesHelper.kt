package de.kuschku.quasseldroid_ng.util.helper

import android.content.res.Resources
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes

@ColorInt
fun Resources.getColorBackport(@ColorRes color: Int, theme: Resources.Theme): Int {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    getColor(color, theme)
  } else {
    // We have to use this method on older systems that don’t yet support the new method
    // which is used above
    getColor(color)
  }
}
