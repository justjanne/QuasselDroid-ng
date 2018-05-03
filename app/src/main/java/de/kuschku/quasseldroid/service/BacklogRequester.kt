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

package de.kuschku.quasseldroid.service

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel

class BacklogRequester(
  private val viewModel: QuasselViewModel,
  private val database: QuasselDatabase
) {
  fun loadMore(accountId: Long, buffer: BufferId, amount: Int, pageSize: Int,
               lastMessageId: MsgId? = null,
               untilVisible: Boolean = false,
               finishCallback: () -> Unit) {
    var missing = amount
    viewModel.session.value?.orNull()?.backlogManager?.let {
      it.requestBacklog(
        bufferId = buffer,
        last = lastMessageId ?: database.message().findFirstByBufferId(
          buffer
        )?.messageId ?: -1,
        limit = amount
      ) {
        if (untilVisible && it.isNotEmpty()) {
          val filtered = database.filtered().get(accountId, buffer) ?: 0
          missing -= it.count {
            (it.type.value and filtered.inv()) != 0
          }
          val messageId = it.map(Message::messageId).min()
          if (missing > 0) {
            loadMore(accountId, buffer, missing, pageSize, messageId, untilVisible, finishCallback)
          } else {
            finishCallback()
          }
        } else {
          finishCallback()
        }
      }
    }
  }
}
