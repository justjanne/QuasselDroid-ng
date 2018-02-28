package de.kuschku.quasseldroid_ng.ui.settings.data

import android.support.annotation.StyleRes
import de.kuschku.quasseldroid_ng.R

data class AppearanceSettings(
  val showPrefix: ShowPrefixMode = ShowPrefixMode.HIGHEST,
  val colorizeNicknames: ColorizeNicknamesMode = ColorizeNicknamesMode.ALL_BUT_MINE,
  val inputEnter: InputEnterMode = InputEnterMode.EMOJI,
  val colorizeMirc: Boolean = true,
  val useMonospace: Boolean = false,
  val showSeconds: Boolean = false,
  val use24hClock: Boolean = true,
  val showHostmask: Boolean = false,
  val showLag: Boolean = true,
  val theme: Theme = Theme.QUASSEL_LIGHT
) {
  enum class ColorizeNicknamesMode {
    ALL,
    ALL_BUT_MINE,
    NONE;

    companion object {
      private val map = values().associateBy { it.name }
      fun of(name: String) = map[name]
    }
  }

  enum class InputEnterMode {
    EMOJI,
    SEND;

    companion object {
      private val map = values().associateBy { it.name }
      fun of(name: String) = map[name]
    }
  }

  enum class ShowPrefixMode {
    ALL,
    HIGHEST,
    NONE;

    companion object {
      private val map = values().associateBy { it.name }
      fun of(name: String) = map[name]
    }
  }

  enum class Theme(@StyleRes val style: Int) {
    QUASSEL_LIGHT(R.style.Theme_ChatTheme_Quassel_Light),
    QUASSEL_DARK(R.style.Theme_ChatTheme_Quassel_Dark),
    AMOLED(R.style.Theme_ChatTheme_Amoled),
    SOLARIZED_LIGHT(R.style.Theme_ChatTheme_Solarized_Light),
    SOLARIZED_DARK(R.style.Theme_ChatTheme_Solarized_Dark),
    GRUVBOX_LIGHT(R.style.Theme_ChatTheme_Gruvbox_Light),
    GRUVBOX_DARK(R.style.Theme_ChatTheme_Gruvbox_Dark);

    companion object {
      private val map = values().associateBy { it.name }
      fun of(name: String) = map[name]
    }
  }

  companion object {
    val DEFAULT = AppearanceSettings()
  }
}