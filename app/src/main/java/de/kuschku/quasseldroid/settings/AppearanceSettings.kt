/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.settings

import androidx.annotation.StyleRes
import de.kuschku.quasseldroid.R

data class AppearanceSettings(
  val inputEnter: InputEnterMode = InputEnterMode.NEWLINE,
  val showLag: Boolean = true,
  val theme: Theme = Theme.MATERIAL_LIGHT,
  val language: String = "",
  val keepScreenOn: Boolean = false,
  val deceptiveNetworks: Boolean = true
) {
  enum class InputEnterMode {
    EMOJI,
    SEND,
    NEWLINE;

    companion object {
      private val map = values().associateBy { it.name }
      fun of(name: String) = map[name]
    }
  }

  enum class Theme(@StyleRes val style: Int) {
    MATERIAL_DAYNIGHT(R.style.Theme_ChatTheme_Material_DayNight),
    MATERIAL_LIGHT(R.style.Theme_ChatTheme_Material_Light),
    MATERIAL_DARK(R.style.Theme_ChatTheme_Material_Dark),
    QUASSEL_DAYNIGHT(R.style.Theme_ChatTheme_Quassel_DayNight),
    QUASSEL_LIGHT(R.style.Theme_ChatTheme_Quassel_Light),
    QUASSEL_DARK(R.style.Theme_ChatTheme_Quassel_Dark),
    AMOLED(R.style.Theme_ChatTheme_Amoled),
    SOLARIZED_DAYNIGHT(R.style.Theme_ChatTheme_Solarized_DayNight),
    SOLARIZED_LIGHT(R.style.Theme_ChatTheme_Solarized_Light),
    SOLARIZED_DARK(R.style.Theme_ChatTheme_Solarized_Dark),
    GRUVBOX_DAYNIGHT(R.style.Theme_ChatTheme_Gruvbox_DayNight),
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
