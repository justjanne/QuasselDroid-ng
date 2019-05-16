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

package de.kuschku.quasseldroid.viewmodel.data

import android.graphics.drawable.Drawable
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork

sealed class AutoCompleteItem(open val name: String, val suffix: String, private val type: Int) :
  Comparable<AutoCompleteItem> {
  override fun compareTo(other: AutoCompleteItem) = when {
    this.type != other.type -> this.type.compareTo(other.type)
    else                    -> this.name.compareTo(other.name)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AutoCompleteItem) return false

    if (name != other.name) return false
    if (suffix != other.suffix) return false
    if (type != other.type) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + suffix.hashCode()
    result = 31 * result + type
    return result
  }


  data class UserItem(
    val nick: String,
    val hostMask: String,
    val modes: String,
    val lowestMode: Int,
    val realname: CharSequence,
    val away: Boolean,
    val self: Boolean,
    val networkCasemapping: String?,
    val avatarUrls: List<Avatar> = emptyList(),
    val fallbackDrawable: Drawable? = null,
    val displayNick: CharSequence? = null
  ) : AutoCompleteItem(nick, ": ", 0) {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as UserItem

      if (nick != other.nick) return false
      if (hostMask != other.hostMask) return false
      if (modes != other.modes) return false
      if (lowestMode != other.lowestMode) return false
      if (realname != other.realname) return false
      if (away != other.away) return false
      if (self != other.self) return false
      if (networkCasemapping != other.networkCasemapping) return false
      if (avatarUrls != other.avatarUrls) return false
      if (displayNick != other.displayNick) return false

      return true
    }

    override fun hashCode(): Int {
      var result = nick.hashCode()
      result = 31 * result + hostMask.hashCode()
      result = 31 * result + modes.hashCode()
      result = 31 * result + lowestMode
      result = 31 * result + realname.hashCode()
      result = 31 * result + away.hashCode()
      result = 31 * result + self.hashCode()
      result = 31 * result + (networkCasemapping?.hashCode() ?: 0)
      result = 31 * result + avatarUrls.hashCode()
      result = 31 * result + (displayNick?.hashCode() ?: 0)
      return result
    }
  }

  data class AliasItem(
    val alias: String?,
    val expansion: String?
  ) : AutoCompleteItem(alias ?: "", " ", 1)

  data class ChannelItem(
    val info: BufferInfo,
    val network: INetwork.NetworkInfo,
    val bufferStatus: BufferStatus,
    val description: CharSequence,
    val icon: Drawable? = null
  ) : AutoCompleteItem(info.bufferName ?: "", " ", 2) {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as ChannelItem

      if (info != other.info) return false
      if (network != other.network) return false
      if (bufferStatus != other.bufferStatus) return false
      if (description != other.description) return false

      return true
    }

    override fun hashCode(): Int {
      var result = info.hashCode()
      result = 31 * result + network.hashCode()
      result = 31 * result + bufferStatus.hashCode()
      result = 31 * result + description.hashCode()
      return result
    }
  }
}
