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
package de.kuschku.quasseldroid.persistence

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.MsgId

inline fun QuasselDatabase.MessageDao.buffers() =
  _buffers().map { BufferId(it) }

inline fun QuasselDatabase.MessageDao.findByBufferId(bufferId: BufferId) =
  _findByBufferId(bufferId.id)

inline fun QuasselDatabase.MessageDao.findByBufferIdPaged(bufferId: BufferId, type: Int) =
  _findByBufferIdPaged(bufferId.id, type)

inline fun QuasselDatabase.MessageDao.findLastByBufferId(bufferId: BufferId) =
  _findLastByBufferId(bufferId.id)

inline fun QuasselDatabase.MessageDao.lastMsgId(bufferId: BufferId) =
  _lastMsgId(bufferId.id)

inline fun QuasselDatabase.MessageDao.firstMsgId(bufferId: BufferId) =
  _firstMsgId(bufferId.id).map(::MsgId)

inline fun QuasselDatabase.MessageDao.firstVisibleMsgId(bufferId: BufferId, type: Int) =
  _firstVisibleMsgId(bufferId.id, type)?.let(::MsgId)

inline fun QuasselDatabase.MessageDao.findFirstByBufferId(bufferId: BufferId) =
  _findFirstByBufferId(bufferId.id)

inline fun QuasselDatabase.MessageDao.hasVisibleMessages(bufferId: BufferId, type: Int) =
  _hasVisibleMessages(bufferId.id, type)

inline fun QuasselDatabase.MessageDao.merge(bufferId1: BufferId, bufferId2: BufferId) =
  _merge(bufferId1.id, bufferId2.id)

inline fun QuasselDatabase.MessageDao.bufferSize(bufferId: BufferId) =
  _bufferSize(bufferId.id)

inline fun QuasselDatabase.NotificationDao.buffers() =
  _buffers().map(::BufferId)

inline fun QuasselDatabase.NotificationDao.all(bufferId: BufferId) =
  _all(bufferId.id)

inline fun QuasselDatabase.NotificationDao.markHidden(bufferId: BufferId, messageId: MsgId) =
  _markHidden(bufferId.id, messageId.id)

inline fun QuasselDatabase.NotificationDao.markHiddenNormal(bufferId: BufferId) =
  _markHiddenNormal(bufferId.id)

inline fun QuasselDatabase.NotificationDao.markRead(bufferId: BufferId, messageId: MsgId) =
  _markRead(bufferId.id, messageId.id)

inline fun QuasselDatabase.NotificationDao.markReadNormal(bufferId: BufferId) =
  _markReadNormal(bufferId.id)

inline fun QuasselDatabase.FilteredDao.buffers(accountId: Long) =
  _buffers(accountId).map(::BufferId)

inline fun QuasselDatabase.FilteredDao.setFiltered(accountId: Long, bufferId: BufferId,
                                                   filtered: Int) =
  _setFiltered(accountId, bufferId.id, filtered)

inline fun QuasselDatabase.FilteredDao.get(accountId: Long, bufferId: BufferId, defaultValue: Int) =
  _get(accountId, bufferId.id, defaultValue)

inline fun QuasselDatabase.FilteredDao.listen(accountId: Long, bufferId: BufferId,
                                              defaultValue: Int) =
  _listen(accountId, bufferId.id, defaultValue)

inline fun QuasselDatabase.FilteredDao.clear(accountId: Long, bufferId: BufferId) =
  _clear(accountId, bufferId.id)
