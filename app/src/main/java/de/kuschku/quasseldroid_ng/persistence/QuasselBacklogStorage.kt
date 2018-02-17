package de.kuschku.quasseldroid_ng.persistence

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.session.BacklogStorage

class QuasselBacklogStorage(private val db: QuasselDatabase) : BacklogStorage {
  override fun storeMessages(vararg messages: Message, initialLoad: Boolean)
    = storeMessages(messages.asIterable(), initialLoad)

  override fun storeMessages(messages: Iterable<Message>, initialLoad: Boolean) {
    if (initialLoad)
      for ((bufferId, bufferMessages) in messages.sortedBy { it.messageId }.groupBy { it.bufferInfo.bufferId }) {
        val lastMessageId = db.message().findLastByBufferId(bufferId)?.messageId
        val firstMessage = bufferMessages.firstOrNull()
        if (lastMessageId == null || firstMessage == null || lastMessageId < firstMessage.messageId) {
          db.message().clearMessages(bufferId)
        }
      }

    for (message in messages) {
      db.message().save(
        QuasselDatabase.DatabaseMessage(
          messageId = message.messageId,
          time = message.time,
          type = message.type.value,
          flag = message.flag.value,
          bufferId = message.bufferInfo.bufferId,
          sender = message.sender,
          senderPrefixes = message.senderPrefixes,
          content = message.content
        )
      )
    }
  }

  override fun clearMessages(bufferId: BufferId, idRange: IntRange) {
    db.message().clearMessages(bufferId, idRange)
  }

  override fun clearMessages(bufferId: BufferId) {
    db.message().clearMessages(bufferId)
  }

  override fun clearMessages() {
    db.message().clearMessages()
  }

}