package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Message

interface BacklogStorage {
  fun storeMessages(vararg messages: Message, initialLoad: Boolean = false)
  fun storeMessages(messages: Iterable<Message>, initialLoad: Boolean = false)

  fun clearMessages(bufferId: BufferId, idRange: IntRange)

  fun clearMessages(bufferId: BufferId)

  fun clearMessages()
}