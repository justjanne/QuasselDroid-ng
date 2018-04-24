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

package de.kuschku.quasseldroid.viewmodel.data

import android.graphics.drawable.Drawable
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork

sealed class AutoCompleteItem(open val name: String) : Comparable<AutoCompleteItem> {
  override fun compareTo(other: AutoCompleteItem): Int {
    return when {
      this is UserItem &&
      other is ChannelItem -> -1
      this is ChannelItem &&
      other is UserItem    -> 1
      else                 -> this.name.compareTo(other.name)
    }
  }

  data class UserItem(
    val nick: String,
    val modes: String,
    val lowestMode: Int,
    val realname: CharSequence,
    val away: Boolean,
    val networkCasemapping: String?,
    val avatarUrls: List<String> = emptyList(),
    val fallbackDrawable: Drawable? = null,
    val displayNick: CharSequence? = null
  ) : AutoCompleteItem(nick)

  data class ChannelItem(
    val info: BufferInfo,
    val network: INetwork.NetworkInfo,
    val bufferStatus: BufferStatus,
    val description: CharSequence
  ) : AutoCompleteItem(info.bufferName ?: "")
}
