package de.kuschku.quasseldroid_ng.util.helper

import android.content.Context
import android.os.Build

fun Context.getStatusBarHeight(): Int {
  var result = 0
  val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
  if (resourceId > 0) {
    result = resources.getDimensionPixelSize(resourceId)
  }
  return result
}

inline fun <reified T> Context.systemService(): T = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
  getSystemService(T::class.java)
} else {
  getSystemService(T::class.java.simpleName) as T
}
