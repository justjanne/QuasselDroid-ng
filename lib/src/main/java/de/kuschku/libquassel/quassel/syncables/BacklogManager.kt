package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.quassel.syncables.interfaces.IBacklogManager
import de.kuschku.libquassel.session.SignalProxy

class BacklogManager constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "BacklogManager"), IBacklogManager {
  override fun receiveBacklog(bufferId: BufferId, first: MsgId, last: MsgId, limit: Int,
                              additional: Int, messages: QVariantList) {
  }

  override fun receiveBacklogAll(first: MsgId, last: MsgId, limit: Int, additional: Int,
                                 messages: QVariantList) {
  }
}
