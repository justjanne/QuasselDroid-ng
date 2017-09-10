package de.kuschku.quasseldroid_ng.quassel.syncables

import de.kuschku.quasseldroid_ng.protocol.BufferId
import de.kuschku.quasseldroid_ng.protocol.MsgId
import de.kuschku.quasseldroid_ng.protocol.QVariantList
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.IBacklogManager
import de.kuschku.quasseldroid_ng.session.SignalProxy

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
