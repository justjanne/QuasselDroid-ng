package de.kuschku.quasseldroid.settings

import android.support.annotation.StyleRes
import de.kuschku.quasseldroid.R

data class AppearanceSettings(
  val inputEnter: InputEnterMode = InputEnterMode.EMOJI,
  val showLag: Boolean = true,
  val theme: Theme = Theme.QUASSEL_LIGHT
) {
  enum class InputEnterMode {
    EMOJI,
    SEND;

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
    GRUVBOX_DARK(R.style.Theme_ChatTheme_Gruvbox_Dark),
    DRACULA(R.style.Theme_ChatTheme_Dracula);

    companion object {
      private val map = values().associateBy { it.name }
      fun of(name: String) = map[name]
    }
  }

  companion object {
    val DEFAULT = AppearanceSettings()
  }
}
