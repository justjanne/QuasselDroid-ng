package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.syncables.interfaces.IBacklogManager
import de.kuschku.libquassel.session.BacklogStorage
import de.kuschku.libquassel.session.Session

class BacklogManager(
  private val session: Session,
  private val backlogStorage: BacklogStorage
) : SyncableObject(session, "BacklogManager"), IBacklogManager {
  init {
    initialized = true
  }

  fun updateIgnoreRules() = backlogStorage.updateIgnoreRules(session)

  override fun receiveBacklog(bufferId: BufferId, first: MsgId, last: MsgId, limit: Int,
                              additional: Int, messages: QVariantList) {
    backlogStorage.storeMessages(session, messages.mapNotNull(QVariant_::value), initialLoad = true)
  }

  override fun receiveBacklogAll(first: MsgId, last: MsgId, limit: Int, additional: Int,
                                 messages: QVariantList) {
    backlogStorage.storeMessages(session, messages.mapNotNull(QVariant_::value), initialLoad = true)
  }

  fun removeBuffer(buffer: BufferId) {
    backlogStorage.clearMessages(buffer)
  }
}
