package de.kuschku.quasseldroid.ui.chat.info

import android.support.annotation.DrawableRes

data class InfoProperty(
  val name: CharSequence? = null,
  @DrawableRes val icon: Int? = null,
  val value: CharSequence,
  val actions: List<InfoPropertyAction> = emptyList()
)