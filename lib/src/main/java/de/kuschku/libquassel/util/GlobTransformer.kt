/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.util

object GlobTransformer {
  /**
   * Converts a standard POSIX Shell globbing pattern into a regular expression
   * pattern. The result can be used with the standard {@link java.util.regex} API to
   * recognize strings which match the glob pattern.
   * <p/>
   * See also, the POSIX Shell language:
   * http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_13_01
   *
   * @param pattern A glob pattern.
   * @return A regex pattern to recognize the given glob pattern.
   */
  fun convertGlobToRegex(pattern: String): String {
    val sb = StringBuilder(pattern.length)
    var inGroup = 0
    var inClass = 0
    var firstIndexInClass = -1
    val arr = pattern.toCharArray()
    var i = 0
    while (i < arr.size) {
      val ch = arr[i]
      when (ch) {
        '\\'          ->
          if (++i >= arr.size) {
            sb.append('\\')
          } else {
            val next = arr[i]
            when (next) {
              ','      -> {
              }
              'Q', 'E' -> {
                // extra escape needed
                sb.append('\\')
                sb.append('\\')
              }
              else     -> sb.append('\\')
            }// escape not needed
            sb.append(next)
          }
        '*'           -> sb.append(if (inClass == 0) ".*" else '*')
        '?'           -> sb.append(if (inClass == 0) '.' else '?')
        '['           -> {
          inClass++
          firstIndexInClass = i + 1
          sb.append('[')
        }
        ']'           -> {
          inClass--
          sb.append(']')
        }
        '.', '(', ')',
        '+', '|', '^',
        '$', '@', '%' -> {
          if (inClass == 0 || firstIndexInClass == i && ch == '^')
            sb.append('\\')
          sb.append(ch)
        }
        '!'           ->
          sb.append(if (firstIndexInClass == i) '^' else '!')
        '{'           -> {
          inGroup++
          sb.append('(')
        }
        '}'           -> {
          inGroup--
          sb.append(')')
        }
        ','           -> sb.append(if (inGroup > 0) '|' else ',')
        else          -> sb.append(ch)
      }
      i++
    }
    return sb.toString()
  }
}
