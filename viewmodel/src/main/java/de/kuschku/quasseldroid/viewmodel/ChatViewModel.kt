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

package de.kuschku.quasseldroid.viewmodel

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.quasseldroid.viewmodel.data.FormattedMessage
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

open class ChatViewModel : QuasselViewModel() {
  val stateReset = BehaviorSubject.create<Unit>()
  val selectedMessages = BehaviorSubject.createDefault(emptyMap<MsgId, FormattedMessage>())
  val bufferSearch = BehaviorSubject.createDefault("")
  val expandedMessages = BehaviorSubject.createDefault(emptySet<MsgId>())
  val buffer = BehaviorSubject.createDefault(BufferId.MAX_VALUE)
  val bufferOpened = PublishSubject.create<Unit>()
  val bufferViewConfigId = BehaviorSubject.createDefault(-1)
  val recentlySentMessages = BehaviorSubject.createDefault(emptyList<CharSequence>())
  val showHidden = BehaviorSubject.createDefault(false)
  val bufferSearchTemporarilyVisible = BehaviorSubject.createDefault(false)
  val expandedNetworks = BehaviorSubject.createDefault(emptyMap<NetworkId, Boolean>())
  val selectedBufferId = BehaviorSubject.createDefault(BufferId.MAX_VALUE)

  fun resetAccount() {
    bufferViewConfigId.onNext(-1)
    selectedMessages.onNext(emptyMap())
    expandedMessages.onNext(emptySet())
    recentlySentMessages.onNext(emptyList())
    stateReset.onNext(Unit)
  }

  fun selectedMessagesToggle(key: MsgId, value: FormattedMessage): Int {
    val set = selectedMessages.value.orEmpty()
    val result = if (set.containsKey(key)) set - key else set + Pair(key, value)
    selectedMessages.onNext(result)
    return result.size
  }

  fun addRecentlySentMessage(message: CharSequence) {
    recentlySentMessages.onNext(
      listOf(message) + recentlySentMessages.value
        .orEmpty()
        .filter { it != message }
        .take(MAX_RECENT_MESSAGES - 1)
    )
  }

  companion object {
    const val MAX_RECENT_MESSAGES = 20
  }
}
