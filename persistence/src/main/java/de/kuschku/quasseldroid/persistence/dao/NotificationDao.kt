/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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
@file:Suppress("NOTHING_TO_INLINE")

package de.kuschku.quasseldroid.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.BufferId_Type
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.protocol.MsgId_Type
import de.kuschku.quasseldroid.persistence.models.NotificationData

@Dao
interface NotificationDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun save(vararg entities: NotificationData)

  @Query("SELECT DISTINCT bufferId FROM notification")
  fun _buffers(): List<BufferId_Type>

  @Query("SELECT * FROM notification WHERE hidden = 0 ORDER BY time ASC")
  fun all(): List<NotificationData>

  @Query("SELECT * FROM notification WHERE bufferId = :bufferId AND hidden = 0 ORDER BY time ASC")
  fun _all(bufferId: BufferId_Type): List<NotificationData>

  @Query("UPDATE notification SET hidden = 1 WHERE bufferId = :bufferId AND messageId <= :messageId")
  fun _markHidden(bufferId: BufferId_Type, messageId: MsgId_Type)

  @Query("UPDATE notification SET hidden = 1 WHERE bufferId = :bufferId AND flag & 2 = 0")
  fun _markHiddenNormal(bufferId: BufferId_Type)

  @Query("DELETE FROM notification WHERE bufferId = :bufferId AND messageId <= :messageId")
  fun _markRead(bufferId: BufferId_Type, messageId: MsgId_Type)

  @Query("DELETE FROM notification WHERE bufferId = :bufferId AND flag & 2 = 0")
  fun _markReadNormal(bufferId: BufferId_Type)

  @Query("DELETE FROM notification")
  fun clear()
}

inline fun NotificationDao.buffers() =
  _buffers().map(::BufferId)

inline fun NotificationDao.all(bufferId: BufferId) =
  _all(bufferId.id)

inline fun NotificationDao.markHidden(bufferId: BufferId, messageId: MsgId) =
  _markHidden(bufferId.id, messageId.id)

inline fun NotificationDao.markHiddenNormal(bufferId: BufferId) =
  _markHiddenNormal(bufferId.id)

inline fun NotificationDao.markRead(bufferId: BufferId, messageId: MsgId) =
  _markRead(bufferId.id, messageId.id)

inline fun NotificationDao.markReadNormal(bufferId: BufferId) =
  _markReadNormal(bufferId.id)
