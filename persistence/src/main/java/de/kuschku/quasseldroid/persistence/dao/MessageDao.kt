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
@file:Suppress("NOTHING_TO_INLINE")

package de.kuschku.quasseldroid.persistence.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.kuschku.libquassel.protocol.*
import de.kuschku.quasseldroid.persistence.models.MessageData
import io.reactivex.Flowable

@Dao
interface MessageDao {
  @Query("SELECT * FROM message")
  fun all(): List<MessageData>

  @Query("SELECT DISTINCT bufferId FROM message")
  fun _buffers(): List<BufferId_Type>

  @Query("SELECT * FROM message WHERE messageId = :messageId")
  fun find(messageId: MsgId_Type): MessageData?

  @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC")
  fun _findByBufferId(bufferId: BufferId_Type): List<MessageData>

  @Query("SELECT * FROM message WHERE (bufferId = :bufferId AND type & ~ :type > 0 AND ignored = 0) OR (networkId = :networkId AND type & 2 > 0 AND ignored = 0 AND currentBufferId = :bufferId AND currentBufferType = 4 AND :showUserNotices != 0) OR (bufferId = :serverBufferId AND type & 2 > 0 AND ignored = 0 AND currentBufferId = :bufferId AND currentBufferType = 1 AND :showServerNotices != 0) OR (bufferId = :serverBufferId AND type & 4096 > 0 AND ignored = 0 AND currentBufferId = :bufferId AND :showErrors != 0) ORDER BY messageId DESC")
  fun _findByBufferIdPaged(networkId: NetworkId_Type, serverBufferId: BufferId_Type,
                           bufferId: BufferId_Type, type: Int,
                           showUserNotices: Boolean, showServerNotices: Boolean,
                           showErrors: Boolean): DataSource.Factory<Int, MessageData>

  @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId DESC LIMIT 1")
  fun _findLastByBufferId(bufferId: BufferId_Type): MessageData?

  @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId DESC LIMIT 1")
  fun _lastMsgId(bufferId: BufferId_Type): LiveData<MessageData>

  @Query("SELECT messageId FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC LIMIT 1")
  fun _firstMsgId(bufferId: BufferId_Type): Flowable<MsgId_Type>

  @Query("SELECT messageId FROM message WHERE (bufferId = :bufferId AND type & ~ :type > 0 AND ignored = 0) OR (networkId = :networkId AND type & 2 > 0 AND ignored = 0 AND currentBufferId = :bufferId AND currentBufferType = 4 AND :showUserNotices != 0) OR (bufferId = :serverBufferId AND type & 2 > 0 AND ignored = 0 AND currentBufferId = :bufferId AND currentBufferType = 1 AND :showServerNotices != 0) OR (bufferId = :serverBufferId AND type & 4096 > 0 AND ignored = 0 AND currentBufferId = :bufferId AND :showErrors != 0) ORDER BY messageId ASC LIMIT 1")
  fun _firstVisibleMsgId(networkId: NetworkId_Type, serverBufferId: BufferId_Type,
                         bufferId: BufferId_Type, type: Int,
                         showUserNotices: Boolean, showServerNotices: Boolean,
                         showErrors: Boolean): MsgId_Type?

  @Query("SELECT * FROM message WHERE bufferId = :bufferId ORDER BY messageId ASC LIMIT 1")
  fun _findFirstByBufferId(bufferId: BufferId_Type): MessageData?

  @Query("SELECT EXISTS(SELECT 1 FROM message WHERE (bufferId = :bufferId AND type & ~ :type > 0 AND ignored = 0) OR (networkId = :networkId AND type & 2 > 0 AND ignored = 0 AND currentBufferId = :bufferId AND currentBufferType = 4 AND :showUserNotices != 0) OR (bufferId = :serverBufferId AND type & 2 > 0 AND ignored = 0 AND currentBufferId = :bufferId AND currentBufferType = 1 AND :showServerNotices != 0) OR (bufferId = :serverBufferId AND type & 4096 > 0 AND ignored = 0 AND currentBufferId = :bufferId AND :showErrors != 0))")
  fun _hasVisibleMessages(networkId: NetworkId_Type, serverBufferId: BufferId_Type,
                          bufferId: BufferId_Type, type: Int,
                          showUserNotices: Boolean, showServerNotices: Boolean,
                          showErrors: Boolean): Boolean

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(vararg entities: MessageData)

  @Query("UPDATE message SET bufferId = :bufferId1 WHERE bufferId = :bufferId2")
  fun _merge(bufferId1: BufferId_Type, bufferId2: BufferId_Type)

  @Query("SELECT count(*) FROM message WHERE bufferId = :bufferId")
  fun _bufferSize(bufferId: BufferId_Type): Int

  @Query("DELETE FROM message")
  fun clearMessages()

  @Query("DELETE FROM message WHERE bufferId = :bufferId")
  fun clearMessages(bufferId: BufferId_Type)

  @Query(
    "DELETE FROM message WHERE bufferId = :bufferId AND messageId >= :first AND messageId <= :last"
  )
  fun clearMessages(bufferId: BufferId_Type, first: MsgId_Type, last: MsgId_Type)
}

inline fun MessageDao.buffers() =
  _buffers().map { BufferId(it) }

inline fun MessageDao.findByBufferId(bufferId: BufferId) =
  _findByBufferId(bufferId.id)

inline fun MessageDao.findByBufferIdPaged(networkId: NetworkId, serverBufferId: BufferId,
                                          bufferId: BufferId, type: Int, showServerNotices: Boolean,
                                          showUserNotices: Boolean, showErrors: Boolean) =
  _findByBufferIdPaged(networkId.id,
                       serverBufferId.id,
                       bufferId.id,
                       type,
                       showServerNotices,
                       showUserNotices,
                       showErrors)

inline fun MessageDao.findLastByBufferId(bufferId: BufferId) =
  _findLastByBufferId(bufferId.id)

inline fun MessageDao.lastMsgId(bufferId: BufferId) =
  _lastMsgId(bufferId.id)

inline fun MessageDao.firstMsgId(bufferId: BufferId) =
  _firstMsgId(bufferId.id).map(::MsgId)

inline fun MessageDao.firstVisibleMsgId(networkId: NetworkId, serverBufferId: BufferId,
                                        bufferId: BufferId, type: Int, showUserNotices: Boolean,
                                        showServerNotices: Boolean, showErrors: Boolean) =
  _firstVisibleMsgId(networkId.id,
                     serverBufferId.id,
                     bufferId.id,
                     type,
                     showUserNotices,
                     showServerNotices,
                     showErrors)?.let(::MsgId)

inline fun MessageDao.findFirstByBufferId(bufferId: BufferId) =
  _findFirstByBufferId(bufferId.id)

inline fun MessageDao.hasVisibleMessages(networkId: NetworkId, serverBufferId: BufferId,
                                         bufferId: BufferId, type: Int, showUserNotices: Boolean,
                                         showServerNotices: Boolean, showErrors: Boolean) =
  _hasVisibleMessages(networkId.id,
                      serverBufferId.id,
                      bufferId.id,
                      type,
                      showUserNotices,
                      showServerNotices,
                      showErrors)

inline fun MessageDao.merge(bufferId1: BufferId, bufferId2: BufferId) =
  _merge(bufferId1.id, bufferId2.id)

inline fun MessageDao.bufferSize(bufferId: BufferId) =
  _bufferSize(bufferId.id)
