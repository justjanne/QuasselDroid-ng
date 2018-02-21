package de.kuschku.quasseldroid_ng.ui.settings.data

import android.support.annotation.StyleRes
import de.kuschku.quasseldroid_ng.R

data class AppearanceSettings(
  val showPrefix: ShowPrefixMode = ShowPrefixMode.FIRST,
  val colorizeNicknames: ColorizeNicknamesMode = ColorizeNicknamesMode.ALL_BUT_MINE,
  val colorizeMirc: Boolean = true,
  val timeFormat: String = "",
  val showLag: Boolean = true,
  val theme: Theme = Theme.QUASSEL_LIGHT
) {
  enum class ColorizeNicknamesMode {
    ALL,
    ALL_BUT_MINE,
    NONE
  }

  enum class ShowPrefixMode {
    ALL,
    FIRST,
    NONE
  }

  enum class Theme(@StyleRes val style: Int) {
    QUASSEL_LIGHT(R.style.Theme_ChatTheme_Quassel_Light),
    QUASSEL_DARK(R.style.Theme_ChatTheme_Quassel_Dark),
    SOLARIZED_LIGHT(R.style.Theme_ChatTheme_Solarized_Light),
    SOLARIZED_DARK(R.style.Theme_ChatTheme_Solarized_Dark),
    AMOLED(R.style.Theme_ChatTheme_Amoled)
  }
}