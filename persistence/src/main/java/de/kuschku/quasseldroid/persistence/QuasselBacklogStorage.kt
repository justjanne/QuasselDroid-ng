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

package de.kuschku.quasseldroid.persistence

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.libquassel.session.BacklogStorage
import de.kuschku.libquassel.session.Session

class QuasselBacklogStorage(private val db: QuasselDatabase) : BacklogStorage {
  override fun updateIgnoreRules(session: Session) {
    db.message().save(
      *db.message().all().map {
        it.copy(ignored = isIgnored(session, it))
      }.toTypedArray()
    )
  }

  override fun storeMessages(session: Session, vararg messages: Message, initialLoad: Boolean) =
    storeMessages(session, messages.asIterable(), initialLoad)

  override fun storeMessages(session: Session, messages: Iterable<Message>, initialLoad: Boolean) {
    db.message().save(*messages.map {
      QuasselDatabase.MessageData(
        messageId = it.messageId,
        time = it.time,
        type = it.type,
        flag = it.flag,
        bufferId = it.bufferInfo.bufferId,
        sender = it.sender,
        senderPrefixes = it.senderPrefixes,
        realName = it.realName,
        avatarUrl = it.avatarUrl,
        content = it.content,
        ignored = isIgnored(session, it)
      )
    }.toTypedArray())
  }

  override fun clearMessages(bufferId: BufferId, idRange: IntRange) {
    db.message().clearMessages(bufferId, idRange.first, idRange.last)
  }

  override fun clearMessages(bufferId: BufferId) {
    db.message().clearMessages(bufferId)
  }

  override fun clearMessages() {
    db.message().clearMessages()
  }

  private fun isIgnored(session: Session, message: Message): Boolean {
    val bufferName = message.bufferInfo.bufferName ?: ""
    val networkId = message.bufferInfo.networkId
    val networkName = session.network(networkId)?.networkName() ?: ""

    return session.ignoreListManager.match(
      message.content, message.sender, message.type, networkName, bufferName
    ) != IgnoreListManager.StrictnessType.UnmatchedStrictness
  }

  private fun isIgnored(session: Session, message: QuasselDatabase.MessageData): Boolean {
    val bufferInfo = session.bufferSyncer.bufferInfo(message.bufferId)
    val bufferName = bufferInfo?.bufferName ?: ""
    val networkId = bufferInfo?.networkId ?: -1
    val networkName = session.network(networkId)?.networkName() ?: ""

    return session.ignoreListManager.match(
      message.content, message.sender, message.type, networkName, bufferName
    ) != IgnoreListManager.StrictnessType.UnmatchedStrictness
  }
}
