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

package de.kuschku.quasseldroid.service

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.DEBUG
import de.kuschku.libquassel.util.helper.value
import de.kuschku.quasseldroid.persistence.dao.findById
import de.kuschku.quasseldroid.persistence.dao.findFirstByBufferId
import de.kuschku.quasseldroid.persistence.dao.get
import de.kuschku.quasseldroid.persistence.db.AccountDatabase
import de.kuschku.quasseldroid.persistence.db.QuasselDatabase
import de.kuschku.quasseldroid.persistence.util.AccountId
import de.kuschku.quasseldroid.persistence.util.QuasselBacklogStorage
import io.reactivex.Observable

class BacklogRequester(
  private val session: Observable<Optional<ISession>>,
  private val database: QuasselDatabase,
  private val accountDatabase: AccountDatabase
) {
  fun loadMore(accountId: AccountId, buffer: BufferId, amount: Int, pageSize: Int,
               lastMessageId: MsgId? = null,
               untilAllVisible: Boolean = false,
               finishCallback: () -> Unit) {
    log(DEBUG,
        "BacklogRequester",
        "requested(bufferId: $buffer, amount: $amount, pageSize: $pageSize, lastMessageId: $lastMessageId, untilAllVisible: $untilAllVisible)")
    var missing = amount
    session.value?.orNull()?.let { session: ISession ->
      session.backlogManager.let {
        val filtered = database.filtered().get(
          accountId,
          buffer,
          accountDatabase.accounts().findById(accountId)?.defaultFiltered ?: 0
        )
        it.requestBacklog(
          bufferId = buffer,
          last = lastMessageId
                 ?: database.message().findFirstByBufferId(buffer)?.messageId
                 ?: MsgId(-1),
          limit = amount
        ) {
          if (it.isNotEmpty()) {
            missing -= it.count {
              (it.type.value and filtered.toUInt().inv()) != 0u &&
              !QuasselBacklogStorage.isIgnored(session, it)
            }
            val hasLoadedAll = missing == 0
            val hasLoadedAny = missing < amount
            if (untilAllVisible && !hasLoadedAll || !untilAllVisible && !hasLoadedAny) {
              val messageId = it.map(Message::messageId).min()
              loadMore(accountId,
                       buffer,
                       missing,
                       pageSize,
                       messageId,
                       untilAllVisible,
                       finishCallback)
              true
            } else {
              finishCallback()
              true
            }
          } else {
            finishCallback()
            true
          }
        }
      }
    }
  }
}
