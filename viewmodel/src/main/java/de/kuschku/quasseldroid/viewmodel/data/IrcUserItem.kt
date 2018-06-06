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

package de.kuschku.quasseldroid.viewmodel.data

import android.graphics.drawable.Drawable

data class IrcUserItem(
  val nick: String,
  val modes: String,
  val lowestMode: Int,
  val realname: CharSequence,
  val hostmask: String,
  val away: Boolean,
  val self: Boolean,
  val networkCasemapping: String?,
  val avatarUrls: List<Avatar> = emptyList(),
  val initial: String? = "",
  val fallbackDrawable: Drawable? = null,
  val displayNick: CharSequence? = null
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as IrcUserItem

    if (nick != other.nick) return false
    if (modes != other.modes) return false
    if (lowestMode != other.lowestMode) return false
    if (realname != other.realname) return false
    if (hostmask != other.hostmask) return false
    if (away != other.away) return false
    if (self != other.self) return false
    if (networkCasemapping != other.networkCasemapping) return false
    if (avatarUrls != other.avatarUrls) return false
    if (initial != other.initial) return false
    if (displayNick != other.displayNick) return false

    return true
  }

  override fun hashCode(): Int {
    var result = nick.hashCode()
    result = 31 * result + modes.hashCode()
    result = 31 * result + lowestMode
    result = 31 * result + realname.hashCode()
    result = 31 * result + hostmask.hashCode()
    result = 31 * result + away.hashCode()
    result = 31 * result + self.hashCode()
    result = 31 * result + (networkCasemapping?.hashCode() ?: 0)
    result = 31 * result + avatarUrls.hashCode()
    result = 31 * result + (initial?.hashCode() ?: 0)
    result = 31 * result + (displayNick?.hashCode() ?: 0)
    return result
  }
}
