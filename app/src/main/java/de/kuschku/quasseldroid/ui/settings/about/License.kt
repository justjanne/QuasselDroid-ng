package de.kuschku.quasseldroid.ui.settings.about

import android.support.annotation.StringRes

data class License(
  val shortName: String,
  val fullName: String = shortName,
  @StringRes val text: Int
)