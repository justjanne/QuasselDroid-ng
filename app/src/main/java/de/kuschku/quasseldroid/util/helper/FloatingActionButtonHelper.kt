package de.kuschku.quasseldroid.util.helper

import android.support.design.widget.FloatingActionButton

fun FloatingActionButton.toggle(visible: Boolean) {
  if (visible) show()
  else hide()
}