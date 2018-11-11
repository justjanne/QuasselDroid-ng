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
  fun nick(mask: String) = split(mask).first

  fun user(mask: String) = split(mask).second

  fun host(mask: String) = split(mask).third

  fun split(mask: String): Triple<String, String, String> {
    val atIndex = mask.lastIndexOf('@')
    if (atIndex == -1)
      return Triple(mask, "", "")

    val host = mask.substring(atIndex + 1)
    val userPart = mask.substring(0, atIndex)

    val nickUserSplit = userPart.split('!', limit = 2)
    if (nickUserSplit.size < 2)
      return Triple(userPart, "", host)

    val (nick, user) = nickUserSplit
    return Triple(nick, user, host)
  }
}
