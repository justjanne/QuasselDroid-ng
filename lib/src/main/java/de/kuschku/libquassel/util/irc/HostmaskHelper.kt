/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
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

package de.kuschku.libquassel.util.irc

object HostmaskHelper {
  fun nick(mask: String): String {
    val (nick, _, _) = split(mask)
    return nick
  }

  fun user(mask: String): String {
    val (_, user, _) = split(mask)
    return user
  }

  fun host(mask: String): String {
    val (_, _, host) = split(mask)
    return host
  }

  fun split(mask: String): Triple<String, String, String> {
    val userPartHostSplit = mask.split("@", limit = 2)
    if (userPartHostSplit.size < 2)
      return Triple(mask, "", "")

    val (userPart, host) = userPartHostSplit

    val nickUserSplit = userPart.split('!', limit = 2)
    if (nickUserSplit.size < 2)
      return Triple(mask, "", host)

    val (nick, user) = nickUserSplit
    return Triple(nick, user, host)
  }
}
