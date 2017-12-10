package de.kuschku.quasseldroid_ng.util.helper

import de.kuschku.quasseldroid_ng.util.ui.MaterialContentLoadingProgressBar

fun MaterialContentLoadingProgressBar.toggle(visible: Boolean) {
  if (visible) show()
  else hide()
}