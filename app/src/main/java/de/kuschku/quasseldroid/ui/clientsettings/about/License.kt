package de.kuschku.quasseldroid.ui.clientsettings.about

import android.support.annotation.StringRes

data class License(
  val shortName: String,
  val fullName: String = shortName,
  @StringRes val text: Int
)
