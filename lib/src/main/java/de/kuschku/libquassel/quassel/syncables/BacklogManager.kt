package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.syncables.interfaces.IBacklogManager
import de.kuschku.libquassel.session.BacklogStorage
import de.kuschku.libquassel.session.SignalProxy
import java.util.concurrent.atomic.AtomicInteger

class BacklogManager(
  proxy: SignalProxy,
  private val backlogStorage: BacklogStorage
) : SyncableObject(proxy, "BacklogManager"), IBacklogManager {
  init {
    initialized = true
  }

  private var loading = AtomicInteger(-1)

  override fun requestBacklog(bufferId: BufferId, first: MsgId, last: MsgId, limit: Int, additional: Int) {
    if (loading.getAndSet(bufferId) != bufferId) {
      super.requestBacklog(bufferId, first, last, limit, additional)
    }
  }

  override fun requestBacklogAll(first: MsgId, last: MsgId, limit: Int, additional: Int) {
    super.requestBacklogAll(first, last, limit, additional)
  }

  override fun receiveBacklog(bufferId: BufferId, first: MsgId, last: MsgId, limit: Int,
                              additional: Int, messages: QVariantList) {
    loading.compareAndSet(bufferId, -1)
    backlogStorage.storeMessages(messages.mapNotNull(QVariant_::value), initialLoad = true)
  }

  override fun receiveBacklogAll(first: MsgId, last: MsgId, limit: Int, additional: Int,
                                 messages: QVariantList) {
    backlogStorage.storeMessages(messages.mapNotNull(QVariant_::value), initialLoad = true)
  }
}
