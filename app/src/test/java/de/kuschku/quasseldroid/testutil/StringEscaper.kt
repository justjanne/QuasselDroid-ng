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

package de.kuschku.quasseldroid.testutil

object StringEscaper {
  private fun escape(text: String): String {
    val stringBuilder = StringBuilder()
    escape(stringBuilder, text)
    return stringBuilder.toString()
  }

  private fun escape(stringBuilder: StringBuilder, text: String) {
    for (char in text) {
      escape(stringBuilder, char)
    }
  }

  private fun escape(stringBuilder: StringBuilder, text: Char) {
    if (text > '\u007f') {
      // write \udddd
      stringBuilder.append("\\u")
      val hex = StringBuffer(Integer.toHexString(text.toInt()))
      hex.reverse()
      val length = 4 - hex.length
      for (j in 0 until length) {
        hex.append('0')
      }
      for (j in 0..3) {
        stringBuilder.append(hex[3 - j])
      }
    } else {
      stringBuilder.append(Character.toString(text))
    }
  }
}
