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

package de.kuschku.libquassel.util.irc

import java.util.*

object IrcCaseMappers {
  var irc: IrcCaseMapper = ClassicalIrcCaseMapper()
  var unicode: IrcCaseMapper = UnicodeCaseMapper()

  interface IrcCaseMapper {
    fun equalsIgnoreCase(a: String, b: String): Boolean
    fun equalsIgnoreCaseNullable(a: String?, b: String?) = when {
      a === null && b === null -> true
      a === null               -> false
      b === null               -> false
      else                     -> this.equalsIgnoreCase(a, b)
    }

    fun toLowerCase(value: String): String
    fun toLowerCaseNullable(value: String?): String? = value?.let(this@IrcCaseMapper::toLowerCase)

    fun toUpperCase(value: String): String
    fun toUpperCaseNullable(value: String?): String? = value?.let(this@IrcCaseMapper::toUpperCase)
  }

  internal class UnicodeCaseMapper : IrcCaseMapper {

    override fun equalsIgnoreCase(a: String, b: String): Boolean {
      return a.equals(b, ignoreCase = true)
    }

    override fun toLowerCase(value: String): String {
      return value.lowercase(Locale.ROOT)
    }

    override fun toUpperCase(value: String): String {
      return value.uppercase(Locale.ROOT)
    }
  }

  internal class ClassicalIrcCaseMapper :
    IrcCaseMapper {
    override fun toLowerCase(value: String): String {
      return value.lowercase(Locale.ROOT)
        .replace('[', '{')
        .replace(']', '}')
        .replace('^', '~')
    }

    override fun toUpperCase(value: String): String {
      return value.uppercase(Locale.ROOT)
        .replace('{', '[')
        .replace('}', ']')
        .replace('~', '^')
    }

    override fun equalsIgnoreCase(a: String, b: String): Boolean {
      return toLowerCase(a) == toLowerCase(b) || toUpperCase(a) == toUpperCase(b)
    }
  }

  operator fun get(caseMapping: String?) = if (caseMapping.equals("rfc1459", ignoreCase = true)) {
    irc
  } else {
    unicode
  }
}
