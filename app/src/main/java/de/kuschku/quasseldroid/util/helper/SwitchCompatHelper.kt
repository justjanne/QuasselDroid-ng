package de.kuschku.quasseldroid.util.helper

import android.support.v7.widget.SwitchCompat
import android.view.ViewGroup
import de.kuschku.quasseldroid.util.ui.AnimationHelper

fun SwitchCompat.setDependent(view: ViewGroup, reverse: Boolean = false) {
  this.setOnCheckedChangeListener { _, isChecked ->
    if (reverse && !isChecked || !reverse && isChecked) {
      AnimationHelper.expand(view)
    } else {
      AnimationHelper.collapse(view)
    }
  }
}