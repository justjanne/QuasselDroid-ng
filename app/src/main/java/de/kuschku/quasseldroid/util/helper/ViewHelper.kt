package de.kuschku.quasseldroid.util.helper

import android.support.v7.widget.TooltipCompat
import android.view.View

fun View.visibleIf(check: Boolean) = if (check) {
  this.visibility = View.VISIBLE
} else {
  this.visibility = View.GONE
}

fun View.setTooltip(tooltipText: CharSequence? = this.contentDescription) =
  TooltipCompat.setTooltipText(this, tooltipText)
