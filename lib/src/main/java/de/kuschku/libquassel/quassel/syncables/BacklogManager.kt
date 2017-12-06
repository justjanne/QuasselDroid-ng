package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.syncables.interfaces.IBacklogManager
import de.kuschku.libquassel.session.BacklogStorage
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.libquassel.util.compatibility.log

class BacklogManager(
  proxy: SignalProxy,
  private val backlogStorage: BacklogStorage
) : SyncableObject(proxy, "BacklogManager"), IBacklogManager {
  override fun receiveBacklog(bufferId: BufferId, first: MsgId, last: MsgId, limit: Int,
                              additional: Int, messages: QVariantList) {
    for (message: Message in messages.mapNotNull<QVariant_, Message>(QVariant_::value)) {
      if (message.bufferInfo.bufferId != bufferId) {
        // Check if it works here
        log(LoggingHandler.LogLevel.ERROR, "message has inconsistent bufferid: $bufferId != ${message.bufferInfo.bufferId}")
      }
      backlogStorage.storeMessages(message)
    }
  }

  override fun receiveBacklogAll(first: MsgId, last: MsgId, limit: Int, additional: Int,
                                 messages: QVariantList) {
    for (message: Message in messages.mapNotNull<QVariant_, Message>(QVariant_::value)) {
      backlogStorage.storeMessages(message)
    }
  }
}
