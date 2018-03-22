package de.kuschku.quasseldroid.util.helper

import android.view.View

fun View.visibleIf(check: Boolean) = if (check) {
  this.visibility = View.VISIBLE
} else {
  this.visibility = View.GONE
}