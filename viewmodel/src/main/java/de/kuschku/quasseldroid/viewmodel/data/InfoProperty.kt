package de.kuschku.quasseldroid.viewmodel.data

import android.support.annotation.DrawableRes

data class InfoProperty(
  val name: CharSequence? = null,
  @DrawableRes val icon: Int? = null,
  val value: CharSequence
)