/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.settings

import android.support.annotation.StyleRes
import de.kuschku.quasseldroid.R

data class AppearanceSettings(
  val inputEnter: InputEnterMode = InputEnterMode.EMOJI,
  val showLag: Boolean = true,
  val theme: Theme = Theme.MATERIAL_LIGHT
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
    MATERIAL_LIGHT(R.style.Theme_ChatTheme_Material_Light),
    MATERIAL_DARK(R.style.Theme_ChatTheme_Material_Dark),
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
