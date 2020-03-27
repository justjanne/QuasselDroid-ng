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

data class MessageSettings(
  val showPrefix: ShowPrefixMode = ShowPrefixMode.HIGHEST,
  val colorizeNicknames: SenderColorMode = SenderColorMode.ALL_BUT_MINE,
  val colorizeMirc: Boolean = true,
  val useMonospace: Boolean = false,
  val textSize: Int = 14,
  val showSeconds: Boolean = false,
  val use24hClock: Boolean = true,
  val showHostmaskActions: Boolean = false,
  val nicksOnNewLine: Boolean = false,
  val timeAtEnd: Boolean = false,
  val showRealNames: Boolean = false,
  val showAvatars: Boolean = true,
  val squareAvatars: Boolean = true,
  val showIRCCloudAvatars: Boolean = false,
  val showGravatarAvatars: Boolean = false,
  val showMatrixAvatars: Boolean = false,
  val largerEmoji: Boolean = false,
  val highlightOwnMessages: Boolean = false,
  val replaceEmoji: Boolean = true
) {

  enum class SenderColorMode {
    ALL,
    ALL_BUT_MINE,
    NONE;

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

  companion object {
    val DEFAULT = MessageSettings()
  }
}
