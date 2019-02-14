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

package de.kuschku.quasseldroid.persistence.util

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.libquassel.session.BacklogStorage
import de.kuschku.libquassel.session.ISession
import de.kuschku.quasseldroid.persistence.db.QuasselDatabase
import de.kuschku.quasseldroid.persistence.models.MessageData

class QuasselBacklogStorage(private val db: QuasselDatabase) : BacklogStorage {
  override fun updateIgnoreRules(session: ISession) {
    db.message().save(
      *db.message().all().map {
        it.copy(ignored = isIgnored(
          session,
          it))
      }.toTypedArray()
    )
  }

  override fun storeMessages(session: ISession, vararg messages: Message) =
    storeMessages(session, messages.asIterable())

  override fun storeMessages(session: ISession, messages: Iterable<Message>) {
    db.message().save(*messages.map {
      MessageData.of(
        messageId = it.messageId,
        time = it.time,
        type = it.type,
        flag = it.flag,
        bufferId = it.bufferInfo.bufferId,
        networkId = it.bufferInfo.networkId,
        sender = it.sender,
        senderPrefixes = it.senderPrefixes,
        realName = it.realName,
        avatarUrl = it.avatarUrl,
        attachments = it.attachments,
        content = it.content,
        ignored = isIgnored(
          session,
          it)
      )
    }.toTypedArray())
  }

  override fun clearMessages(bufferId: BufferId, idRange: LongRange) {
    db.message().clearMessages(bufferId.id, idRange.first, idRange.last)
  }

  override fun clearMessages(bufferId: BufferId) {
    db.message().clearMessages(bufferId.id)
  }

  override fun clearMessages() {
    db.message().clearMessages()
  }

  companion object {
    fun isIgnored(session: ISession, message: Message): Boolean {
      val bufferName = message.bufferInfo.bufferName ?: ""
      val networkId = message.bufferInfo.networkId
      val networkName = session.network(networkId)?.networkName() ?: ""

      return session.ignoreListManager.match(
        message.content, message.sender, message.type, networkName, bufferName
      ) != IgnoreListManager.StrictnessType.UnmatchedStrictness
    }

    fun isIgnored(session: ISession, message: MessageData): Boolean {
      val bufferInfo = session.bufferSyncer.bufferInfo(message.bufferId)
      val bufferName = bufferInfo?.bufferName ?: ""
      val networkId = bufferInfo?.networkId ?: NetworkId(-1)
      val networkName = session.network(networkId)?.networkName() ?: ""

      return session.ignoreListManager.match(
        message.content, message.sender, message.type, networkName, bufferName
      ) != IgnoreListManager.StrictnessType.UnmatchedStrictness
    }
  }
}
