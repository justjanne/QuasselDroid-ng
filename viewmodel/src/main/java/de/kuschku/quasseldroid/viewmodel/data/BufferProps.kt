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
  val name: CharSequence = "",
  val ircUser: IrcUser? = null,
  val avatarUrls: List<Avatar> = emptyList(),
  val fallbackDrawable: Drawable? = null,
  val matchMode: MatchMode = MatchMode.EXACT
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as BufferProps

    if (info != other.info) return false
    if (network != other.network) return false
    if (networkConnectionState != other.networkConnectionState) return false
    if (bufferStatus != other.bufferStatus) return false
    if (description != other.description) return false
    if (activity != other.activity) return false
    if (highlights != other.highlights) return false
    if (bufferActivity != other.bufferActivity) return false
    if (ircUser != other.ircUser) return false
    if (avatarUrls != other.avatarUrls) return false

    return true
  }

  override fun hashCode(): Int {
    var result = info.hashCode()
    result = 31 * result + network.hashCode()
    result = 31 * result + networkConnectionState.hashCode()
    result = 31 * result + bufferStatus.hashCode()
    result = 31 * result + description.hashCode()
    result = 31 * result + activity.hashCode()
    result = 31 * result + highlights
    result = 31 * result + bufferActivity.hashCode()
    result = 31 * result + (ircUser?.hashCode() ?: 0)
    result = 31 * result + avatarUrls.hashCode()
    return result
  }
}
