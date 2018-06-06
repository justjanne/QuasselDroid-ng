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
import de.kuschku.libquassel.protocol.Buffer_Activities
import de.kuschku.libquassel.protocol.Message_Types
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork

data class BufferProps(
  val info: BufferInfo,
  val network: INetwork.NetworkInfo,
  val networkConnectionState: INetwork.ConnectionState,
  val bufferStatus: BufferStatus,
  val description: CharSequence,
  val activity: Message_Types,
  val highlights: Int = 0,
  val bufferActivity: Buffer_Activities = BufferInfo.Activity.of(
    BufferInfo.Activity.NoActivity
  ),
  val hiddenState: BufferHiddenState,
  val ircUser: IrcUser? = null,
  val avatarUrls: List<Avatar> = emptyList(),
  val fallbackDrawable: Drawable? = null
)
