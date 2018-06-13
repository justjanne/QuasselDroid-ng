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

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.syncables.interfaces.IBacklogManager
import de.kuschku.libquassel.session.BacklogStorage
import de.kuschku.libquassel.session.Session
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.DEBUG

class BacklogManager(
  private val session: Session,
  private val backlogStorage: BacklogStorage
) : SyncableObject(session, "BacklogManager"), IBacklogManager {
  private val loading = mutableMapOf<BufferId, (List<Message>) -> Boolean>()
  private val loadingFiltered = mutableMapOf<BufferId, (List<Message>) -> Boolean>()

  init {
    initialized = true
  }

  fun updateIgnoreRules() = backlogStorage.updateIgnoreRules(session)

  fun requestBacklog(bufferId: BufferId, first: MsgId = -1, last: MsgId = -1, limit: Int = -1,
                     additional: Int = 0, callback: (List<Message>) -> Boolean) {
    if (loading.contains(bufferId)) return
    loading[bufferId] = callback
    requestBacklog(bufferId, first, last, limit, additional)
  }

  fun requestBacklogFiltered(bufferId: BufferId, first: MsgId = -1, last: MsgId = -1,
                             limit: Int = -1, additional: Int = 0, type: Int = -1, flags: Int = -1,
                             callback: (List<Message>) -> Boolean) {
    if (loadingFiltered.contains(bufferId)) return
    loadingFiltered[bufferId] = callback
    requestBacklogFiltered(bufferId, first, last, limit, additional, type, flags)
  }

  fun requestBacklogAll(first: MsgId = -1, last: MsgId = -1, limit: Int = -1, additional: Int = 0,
                        callback: (List<Message>) -> Boolean) {
    if (loading.contains(-1)) return
    loading[-1] = callback
    requestBacklogAll(first, last, limit, additional)
  }

  fun requestBacklogAllFiltered(first: MsgId = -1, last: MsgId = -1, limit: Int = -1,
                                additional: Int = 0, type: Int = -1, flags: Int = -1,
                                callback: (List<Message>) -> Boolean) {
    if (loading.contains(-1)) return
    loadingFiltered[-1] = callback
    requestBacklogAllFiltered(first, last, limit, additional, type, flags)
  }

  override fun receiveBacklog(bufferId: BufferId, first: MsgId, last: MsgId, limit: Int,
                              additional: Int, messages: QVariantList) {
    val list = messages.mapNotNull<QVariant_, Message>(QVariant_::value)
    if (loading.remove(bufferId)?.invoke(list) != false) {
      log(DEBUG, "BacklogManager", "storeMessages(${list.size})")
      backlogStorage.storeMessages(session, list)
    }
  }

  override fun receiveBacklogAll(first: MsgId, last: MsgId, limit: Int, additional: Int,
                                 messages: QVariantList) {
    val list = messages.mapNotNull<QVariant_, Message>(QVariant_::value)
    if (loading.remove(-1)?.invoke(list) != false) {
      log(DEBUG, "BacklogManager", "storeMessages(${list.size})")
      backlogStorage.storeMessages(session, list)
    }
  }

  override fun receiveBacklogFiltered(bufferId: BufferId, first: MsgId, last: MsgId, limit: Int,
                                      additional: Int, type: Int, flags: Int,
                                      messages: QVariantList) {
    val list = messages.mapNotNull<QVariant_, Message>(QVariant_::value)
    if (loadingFiltered.remove(bufferId)?.invoke(list) != false) {
    log(DEBUG, "BacklogManager", "storeMessages(${list.size})")
    backlogStorage.storeMessages(session, list)
    }
  }

  override fun receiveBacklogAllFiltered(first: MsgId, last: MsgId, limit: Int, additional: Int,
                                         type: Int, flags: Int, messages: QVariantList) {
    val list = messages.mapNotNull<QVariant_, Message>(QVariant_::value)
    if (loadingFiltered.remove(-1)?.invoke(list) != false) {
      log(DEBUG, "BacklogManager", "storeMessages(${list.size})")
      backlogStorage.storeMessages(session, list)
    }
  }

  fun removeBuffer(buffer: BufferId) {
    backlogStorage.clearMessages(buffer)
  }
}
