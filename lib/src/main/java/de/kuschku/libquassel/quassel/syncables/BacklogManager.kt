/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
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
import de.kuschku.libquassel.session.NotificationManager
import de.kuschku.libquassel.session.Session
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log

class BacklogManager(
  private val session: Session,
  private val notificationManager: NotificationManager?,
  private val backlogStorage: BacklogStorage
) : SyncableObject(session, "BacklogManager"), IBacklogManager {
  override fun receiveBacklogFiltered(bufferId: BufferId, first: MsgId, last: MsgId, limit: Int,
                                      additional: Int, type: Int, flags: Int,
                                      messages: QVariantList) {
    log(LoggingHandler.LogLevel.ERROR, "DEBUG", "$messages")
    val actualMessages = messages.mapNotNull { it.value<Message?>(null) }
    log(LoggingHandler.LogLevel.ERROR, "DEBUG", "$actualMessages")
    notificationManager?.processMessages(
      session, *actualMessages.toTypedArray()
    )
  }

  override fun receiveBacklogAllFiltered(first: MsgId, last: MsgId, limit: Int, additional: Int,
                                         type: Int, flags: Int, messages: QVariantList) {
    // TODO: Not implemented
  }

  init {
    initialized = true
  }

  fun updateIgnoreRules() = backlogStorage.updateIgnoreRules(session)

  override fun receiveBacklog(bufferId: BufferId, first: MsgId, last: MsgId, limit: Int,
                              additional: Int, messages: QVariantList) {
    backlogStorage.storeMessages(session, messages.mapNotNull(QVariant_::value), initialLoad = true)
  }

  override fun receiveBacklogAll(first: MsgId, last: MsgId, limit: Int, additional: Int,
                                 messages: QVariantList) {
    backlogStorage.storeMessages(session, messages.mapNotNull(QVariant_::value), initialLoad = true)
  }

  fun removeBuffer(buffer: BufferId) {
    backlogStorage.clearMessages(buffer)
  }
}
