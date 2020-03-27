/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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
@file:Suppress("NOTHING_TO_INLINE")

package de.kuschku.quasseldroid.persistence.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import de.kuschku.libquassel.protocol.*
import org.threeten.bp.Instant
import java.io.Serializable

@Entity(tableName = "message",
        indices = [
          Index("bufferId"),
          Index("ignored"),
          Index("currentBufferId"),
          Index("currentBufferType"),
          Index("networkId")
        ])
data class MessageData(
  @PrimaryKey
  @ColumnInfo(name = "messageId")
  var rawMessageId: MsgId_Type,
  var time: Instant,
  var type: Message_Types,
  var flag: Message_Flags,
  @ColumnInfo(name = "bufferId")
  var rawBufferId: BufferId_Type,
  @ColumnInfo(name = "currentBufferId")
  var rawCurrentBufferId: BufferId_Type,
  var currentBufferType: Buffer_Types,
  @ColumnInfo(name = "networkId")
  var rawNetworkId: NetworkId_Type,
  var sender: String,
  var senderPrefixes: String,
  var realName: String,
  var avatarUrl: String,
  var content: String,
  var ignored: Boolean
) : Serializable {
  inline val messageId
    get() = MsgId(rawMessageId)
  inline val bufferId
    get() = BufferId(rawBufferId)
  inline val currentBufferId
    get() = BufferId(rawCurrentBufferId)
  inline val networkId
    get() = NetworkId(rawNetworkId)

  companion object {
    inline fun of(
      messageId: MsgId,
      time: Instant,
      type: Message_Types,
      flag: Message_Flags,
      bufferId: BufferId,
      currentBufferType: Buffer_Types,
      networkId: NetworkId,
      currentBufferId: BufferId,
      sender: String,
      senderPrefixes: String,
      realName: String,
      avatarUrl: String,
      content: String,
      ignored: Boolean
    ) = MessageData(
      messageId.id,
      time,
      type,
      flag,
      bufferId.id,
      currentBufferId.id,
      currentBufferType,
      networkId.id,
      sender,
      senderPrefixes,
      realName,
      avatarUrl,
      content,
      ignored
    )
  }
}
