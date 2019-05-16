/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.quasseldroid.util.compatibility

object AndroidCrashFixer {
  fun removeCrashableCharacters(text: String): String {
    var previousRtlModifier = 0.toChar()
    return text.fold(StringBuilder()) { builder, char ->
      if (char != '\u200E' && char != '\u200F') {
        if (previousRtlModifier != 0.toChar() && !char.isWhitespace()) {
          builder.append(previousRtlModifier)
          previousRtlModifier = 0.toChar()
        }
        builder.append(char)
      } else {
        previousRtlModifier = char
      }
      builder
    }.toString()
  }
}
