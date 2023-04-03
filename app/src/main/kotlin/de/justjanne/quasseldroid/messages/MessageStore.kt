package de.justjanne.quasseldroid.messages

import de.justjanne.libquassel.client.syncables.ClientBacklogManager
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.libquassel.protocol.models.ids.BufferId
import de.justjanne.libquassel.protocol.models.ids.MsgId
import de.justjanne.libquassel.protocol.util.StateHolder
import de.justjanne.libquassel.protocol.variant.into
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Closeable

class MessageStore(
  incoming: Flow<Message>,
  private val backlogManager: ClientBacklogManager
) : Closeable, StateHolder<Map<BufferId, MessageBuffer>> {
  private val state = MutableStateFlow(mapOf<BufferId, MessageBuffer>())
  override fun state() = state.value
  override fun flow() = state

  private val scope = CoroutineScope(Dispatchers.IO)
  private val disposable = incoming.onEach { message ->
    val bufferId = message.bufferInfo.bufferId
    state.update { messages ->
      val buffer = messages[bufferId] ?: MessageBuffer(true, emptyList())
      if (buffer.atEnd) {
        messages + Pair(bufferId, buffer.copy(messages = buffer.messages + message))
      } else {
        messages
      }
    }
  }.launchIn(scope)

  fun loadAround(bufferId: BufferId, messageId: MsgId, limit: Int) {
    scope.launch {
      state.update { messages ->
        val (before, after) = listOf(
          backlogManager.backlog(bufferId, last = messageId, limit = limit)
            .mapNotNull { it.into<Message>() },
          backlogManager.backlogForward(bufferId, first = messageId, limit = limit - 1)
            .mapNotNull { it.into<Message>() },
        )

        val updated = MessageBuffer(
          atEnd = false,
          messages = (before + after).distinct().sortedBy { it.messageId }
        )
        messages + Pair(bufferId, updated)
      }
    }
  }

  fun loadBefore(bufferId: BufferId, limit: Int) {
    scope.launch {
      state.update { messages ->
        val buffer = messages[bufferId] ?: MessageBuffer(true, emptyList())
        val messageId = buffer.messages.firstOrNull()?.messageId ?: MsgId(-1)
        val data = backlogManager.backlog(bufferId, last = messageId, limit = limit)
          .mapNotNull { it.into<Message>() }
        val updated = buffer.copy(
          messages = (buffer.messages + data).distinct().sortedBy { it.messageId }
        )
        messages + Pair(bufferId, updated)
      }
    }
  }

  fun loadAfter(bufferId: BufferId, limit: Int) {
    scope.launch {
      state.update { messages ->
        val buffer = messages[bufferId] ?: MessageBuffer(true, emptyList())
        val messageId = buffer.messages.lastOrNull()?.messageId ?: MsgId(-1)
        val data = backlogManager.backlogForward(bufferId, first = messageId, limit = limit)
          .mapNotNull { it.into<Message>() }
        val updated = buffer.copy(
          messages = (buffer.messages + data).distinct().sortedBy { it.messageId }
        )
        messages + Pair(bufferId, updated)
      }
    }
  }

  fun clear(bufferId: BufferId) {
    scope.launch {
      state.update { messages ->
        messages - bufferId
      }
    }
  }

  override fun close() {
    runBlocking {
      disposable.cancelAndJoin()
    }
  }
}
