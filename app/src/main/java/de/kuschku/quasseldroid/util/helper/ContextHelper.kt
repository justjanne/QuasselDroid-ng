package de.kuschku.quasseldroid.util.helper

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Build
import android.preference.PreferenceManager
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes

fun Context.getStatusBarHeight(): Int {
  var result = 0
  val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
  if (resourceId > 0) {
    result = resources.getDimensionPixelSize(resourceId)
  }
  return result
}

inline fun <reified T> Context.systemService(): T =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    getSystemService(T::class.java)
  } else {
    getSystemService(T::class.java.simpleName) as T
  }

fun Context.getCompatDrawable(@DrawableRes id: Int): Drawable {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    this.resources.getDrawable(id, this.theme)
  } else {
    this.resources.getDrawable(id)
  }
}

@ColorInt
fun Context.getCompatColor(@ColorRes id: Int): Int {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    this.resources.getColor(id, this.theme)
  } else {
    this.resources.getColor(id)
  }
}


fun <T> Context.sharedPreferences(name: String? = null, mode: Int = 0,
                                  f: SharedPreferences.() -> T) =
  if (name == null) {
    PreferenceManager.getDefaultSharedPreferences(this).f()
  } else {
    getSharedPreferences(name, mode).f()
  }