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
import de.kuschku.libquassel.protocol.NetworkId

data class IrcUserItem(
  val networkId: NetworkId,
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
)
