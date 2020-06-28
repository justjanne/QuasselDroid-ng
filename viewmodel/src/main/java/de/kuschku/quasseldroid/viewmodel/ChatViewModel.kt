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

package de.kuschku.quasseldroid.viewmodel

import android.os.Bundle
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helper.safeValue
import de.kuschku.quasseldroid.viewmodel.data.FormattedMessage
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

open class ChatViewModel : QuasselViewModel() {
  val selectedMessages = BehaviorSubject.createDefault(emptyMap<MsgId, FormattedMessage>())
  val bufferSearch = BehaviorSubject.createDefault("")
  val expandedMessages = BehaviorSubject.createDefault(emptySet<MsgId>())
  val bufferId = BehaviorSubject.createDefault(BufferId.MAX_VALUE)
  val bufferViewConfigId = BehaviorSubject.createDefault(-1)
  val recentlySentMessages = BehaviorSubject.createDefault(emptyList<CharSequence>())
  val recentlySentMessageIndex = BehaviorSubject.createDefault(-1)
  val inputCache = BehaviorSubject.createDefault<CharSequence>("")
  val showHidden = BehaviorSubject.createDefault(false)
  val bufferSearchTemporarilyVisible = BehaviorSubject.createDefault(false)
  val expandedNetworks = BehaviorSubject.createDefault(emptyMap<NetworkId, Boolean>())
  val selectedBufferId = BehaviorSubject.createDefault(BufferId.MAX_VALUE)

  val chatToJoin = BehaviorSubject.createDefault(Optional.empty<Pair<NetworkId, String>>())

  val stateReset = BehaviorSubject.create<Unit>()
  val bufferOpened = PublishSubject.create<Unit>()

  val loadKey = BehaviorSubject.create<MsgId>()

  fun onSaveInstanceState(outState: Bundle) {
    /*
    outState.putSerializable(
      KEY_SELECTED_MESSAGES,
      HashMap(selectedMessages.value))
    */
    outState.putString(
      KEY_BUFFER_SEARCH,
      bufferSearch.safeValue)
    outState.putLongArray(
      KEY_EXPANDED_MESSAGES,
      expandedMessages.safeValue.map(MsgId::id).toLongArray())
    outState.putInt(
      KEY_BUFFER_ID,
      bufferId.safeValue.id)
    outState.putInt(
      KEY_BUFFER_VIEW_CONFIG_ID,
      bufferViewConfigId.safeValue)
    outState.putCharSequenceArray(
      KEY_RECENTLY_SENT_MESSAGES,
      recentlySentMessages.safeValue.toTypedArray())
    outState.putBoolean(
      KEY_SHOW_HIDDEN,
      showHidden.safeValue)
    outState.putBoolean(
      KEY_BUFFER_SEARCH_TEMPORARILY_VISIBLE,
      bufferSearchTemporarilyVisible.safeValue)
    outState.putSerializable(
      KEY_EXPANDED_NETWORKS,
      HashMap(expandedNetworks.safeValue))
    outState.putInt(
      KEY_SELECTED_BUFFER_ID,
      selectedBufferId.safeValue.id)
  }

  fun onRestoreInstanceState(savedInstanceState: Bundle) {
    /*
    if (savedInstanceState.containsKey(KEY_SELECTED_MESSAGES)) {
      selectedMessages.onNext(
        savedInstanceState.getSerializable(KEY_SELECTED_MESSAGES) as? HashMap<MsgId, FormattedMessage>
        ?: emptyMap()
      )
    }
    */

    if (savedInstanceState.containsKey(KEY_BUFFER_SEARCH))
      bufferSearch.onNext(savedInstanceState.getString(KEY_BUFFER_SEARCH, null))

    if (savedInstanceState.containsKey(KEY_EXPANDED_MESSAGES))
      expandedMessages.onNext(savedInstanceState.getLongArray(KEY_EXPANDED_MESSAGES)?.map(::MsgId)?.toSet().orEmpty())

    if (savedInstanceState.containsKey(KEY_BUFFER_ID))
      bufferId.onNext(BufferId(savedInstanceState.getInt(KEY_BUFFER_ID)))

    if (savedInstanceState.containsKey(KEY_BUFFER_VIEW_CONFIG_ID))
      bufferViewConfigId.onNext(savedInstanceState.getInt(KEY_BUFFER_VIEW_CONFIG_ID))

    if (savedInstanceState.containsKey(KEY_RECENTLY_SENT_MESSAGES))
      recentlySentMessages.onNext(savedInstanceState.getCharSequenceArray(KEY_RECENTLY_SENT_MESSAGES)?.toList().orEmpty())

    if (savedInstanceState.containsKey(KEY_SHOW_HIDDEN))
      showHidden.onNext(savedInstanceState.getBoolean(KEY_SHOW_HIDDEN))

    if (savedInstanceState.containsKey(KEY_BUFFER_SEARCH_TEMPORARILY_VISIBLE))
      bufferSearchTemporarilyVisible.onNext(savedInstanceState.getBoolean(
        KEY_BUFFER_SEARCH_TEMPORARILY_VISIBLE))

    if (savedInstanceState.containsKey(KEY_EXPANDED_NETWORKS)) {
      expandedNetworks.onNext(
        savedInstanceState.getSerializable(KEY_EXPANDED_NETWORKS) as? HashMap<NetworkId, Boolean>
        ?: emptyMap()
      )
    }

    if (savedInstanceState.containsKey(KEY_SELECTED_BUFFER_ID))
      selectedBufferId.onNext(BufferId(savedInstanceState.getInt(KEY_SELECTED_BUFFER_ID)))
  }

  fun resetAccount() {
    bufferViewConfigId.onNext(-1)
    selectedMessages.onNext(emptyMap())
    expandedMessages.onNext(emptySet())
    recentlySentMessages.onNext(emptyList())
    stateReset.onNext(Unit)
  }

  fun selectedMessagesToggle(key: MsgId, value: FormattedMessage): Int {
    val set = selectedMessages.safeValue
    val result = if (set.containsKey(key)) set - key else set + Pair(key, value)
    selectedMessages.onNext(result)
    return result.size
  }

  fun addRecentlySentMessage(message: CharSequence) {
    recentlySentMessages.onNext(
      listOf(message) + recentlySentMessages.safeValue
        .orEmpty()
        .filter { it != message }
        .take(MAX_RECENT_MESSAGES - 1)
    )
  }

  private fun recentMessagesChange(change: Int) {
    recentlySentMessageIndex.onNext(recentMessagesChangeInternal(
      recentlySentMessageIndex.safeValue,
      recentlySentMessages.safeValue.size,
      change
    ))
  }

  fun recentMessagesValue() =
    if (recentlySentMessageIndex.safeValue == -1) inputCache.safeValue
    else recentlySentMessages.safeValue[recentlySentMessageIndex.safeValue]

  fun recentMessagesIndexDown(content: CharSequence): CharSequence {
    if (recentlySentMessageIndex.safeValue == -1) {
      inputCache.onNext(content)
    }
    recentMessagesChange(+1)
    return recentMessagesValue()
  }

  fun recentMessagesIndexUp(): CharSequence? {
    if (recentlySentMessageIndex.safeValue > -1) {
      recentMessagesChange(-1)
    }
    return recentMessagesValue()
  }

  fun recentMessagesIndexReset(): CharSequence? {
    recentlySentMessageIndex.onNext(-1)
    return recentMessagesValue()
  }

  companion object {
    const val KEY_SELECTED_MESSAGES = "model_chat_selectedMessages"
    const val KEY_BUFFER_SEARCH = "model_chat_bufferSearch"
    const val KEY_EXPANDED_MESSAGES = "model_chat_expandedMessages"
    const val KEY_BUFFER_ID = "model_chat_bufferId"
    const val KEY_BUFFER_VIEW_CONFIG_ID = "model_chat_bufferViewConfigId"
    const val KEY_RECENTLY_SENT_MESSAGES = "model_chat_recentlySentMessages"
    const val KEY_SHOW_HIDDEN = "model_chat_showHidden"
    const val KEY_BUFFER_SEARCH_TEMPORARILY_VISIBLE = "model_chat_bufferSearchTemporarilyVisible"
    const val KEY_EXPANDED_NETWORKS = "model_chat_expandedNetworks"
    const val KEY_SELECTED_BUFFER_ID = "model_chat_selectedBufferId"

    const val MAX_RECENT_MESSAGES = 20

    fun recentMessagesChangeInternal(current: Int, size: Int, change: Int) =
      if (current + change < 0 || size == 0) -1
      else (size + current + change) % size
  }
}
