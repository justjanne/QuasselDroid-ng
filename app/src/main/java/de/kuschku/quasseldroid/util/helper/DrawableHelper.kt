package de.kuschku.quasseldroid.util.helper

import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.graphics.drawable.DrawableCompat

fun Drawable.tint(@ColorInt tint: Int) = DrawableCompat.setTint(this, tint)