package de.kuschku.quasseldroid.util.helper

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat

fun Context.getDrawableCompat(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

fun Context.getVectorDrawableCompat(@DrawableRes id: Int) =
  VectorDrawableCompat.create(this.resources, id, this.theme)

@ColorInt
fun Context.getColorCompat(@ColorRes id: Int) = ContextCompat.getColor(this, id)


fun <T> Context.sharedPreferences(name: String? = null, mode: Int = 0,
                                  f: SharedPreferences.() -> T) =
  if (name == null) {
    PreferenceManager.getDefaultSharedPreferences(this).f()
  } else {
    getSharedPreferences(name, mode).f()
  }
