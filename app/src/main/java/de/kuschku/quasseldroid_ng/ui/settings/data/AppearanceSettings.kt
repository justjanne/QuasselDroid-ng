package de.kuschku.quasseldroid_ng.ui.settings.data

import android.support.annotation.StyleRes
import de.kuschku.quasseldroid_ng.R

data class AppearanceSettings(
  val showPrefix: ShowPrefixMode = ShowPrefixMode.HIGHEST,
  val colorizeNicknames: ColorizeNicknamesMode = ColorizeNicknamesMode.ALL_BUT_MINE,
  val colorizeMirc: Boolean = true,
  val useMonospace: Boolean = false,
  val showSeconds: Boolean = false,
  val use24hClock: Boolean = true,
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
    HIGHEST,
    NONE
  }

  enum class Theme(@StyleRes val style: Int) {
    QUASSEL_LIGHT(R.style.Theme_ChatTheme_Quassel_Light),
    QUASSEL_DARK(R.style.Theme_ChatTheme_Quassel_Dark),
    AMOLED(R.style.Theme_ChatTheme_Amoled),
    SOLARIZED_LIGHT(R.style.Theme_ChatTheme_Solarized_Light),
    SOLARIZED_DARK(R.style.Theme_ChatTheme_Solarized_Dark)
  }

  companion object {
    val DEFAULT = AppearanceSettings()
  }
}