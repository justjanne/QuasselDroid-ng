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
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork

sealed class AutoCompleteItem(open val name: String, private val type: Int) :
  Comparable<AutoCompleteItem> {
  override fun compareTo(other: AutoCompleteItem) = when {
    this.type != other.type -> this.type.compareTo(other.type)
    else                    -> this.name.compareTo(other.name)
  }

  data class UserItem(
    val nick: String,
    val modes: String,
    val lowestMode: Int,
    val realname: CharSequence,
    val away: Boolean,
    val self: Boolean,
    val networkCasemapping: String?,
    val avatarUrls: List<Avatar> = emptyList(),
    val fallbackDrawable: Drawable? = null,
    val displayNick: CharSequence? = null
  ) : AutoCompleteItem(nick, 0)

  data class AliasItem(
    val alias: String,
    val expansion: String
  ) : AutoCompleteItem(alias, 1)

  data class ChannelItem(
    val info: BufferInfo,
    val network: INetwork.NetworkInfo,
    val bufferStatus: BufferStatus,
    val description: CharSequence
  ) : AutoCompleteItem(info.bufferName ?: "", 2)
}
