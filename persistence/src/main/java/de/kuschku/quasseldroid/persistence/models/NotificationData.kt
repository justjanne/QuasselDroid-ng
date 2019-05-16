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

package de.kuschku.quasseldroid.persistence.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import de.kuschku.libquassel.protocol.*
import org.threeten.bp.Instant

@Entity(tableName = "notification", indices = [Index("bufferId")])
data class NotificationData(
  @PrimaryKey
  @ColumnInfo(name = "messageId")
  var rawMessageId: MsgId_Type,
  var creationTime: Instant,
  var time: Instant,
  var type: Message_Types,
  var flag: Message_Flags,
  @ColumnInfo(name = "bufferId")
  var rawBufferId: BufferId_Type,
  var bufferName: String,
  var bufferType: Buffer_Types,
  @ColumnInfo(name = "networkId")
  var rawNetworkId: NetworkId_Type,
  var networkName: String,
  var sender: String,
  var senderPrefixes: String,
  var realName: String,
  var avatarUrl: String,
  var content: String,
  var ownNick: String,
  var ownIdent: String,
  var ownRealName: String,
  var ownAvatarUrl: String,
  var hidden: Boolean
) {
  inline val messageId
    get() = MsgId(rawMessageId)

  inline val bufferId
    get() = BufferId(rawBufferId)

  inline val networkId
    get() = NetworkId(rawNetworkId)

  companion object {
    inline fun of(
      messageId: MsgId,
      creationTime: Instant,
      time: Instant,
      type: Message_Types,
      flag: Message_Flags,
      bufferId: BufferId,
      bufferName: String,
      bufferType: Buffer_Types,
      networkId: NetworkId,
      networkName: String,
      sender: String,
      senderPrefixes: String,
      realName: String,
      avatarUrl: String,
      content: String,
      ownNick: String,
      ownIdent: String,
      ownRealName: String,
      ownAvatarUrl: String,
      hidden: Boolean
    ) = NotificationData(
      messageId.id,
      creationTime,
      time,
      type,
      flag,
      bufferId.id,
      bufferName,
      bufferType,
      networkId.id,
      networkName,
      sender,
      senderPrefixes,
      realName,
      avatarUrl,
      content,
      ownNick,
      ownIdent,
      ownRealName,
      ownAvatarUrl,
      hidden
    )
  }
}
