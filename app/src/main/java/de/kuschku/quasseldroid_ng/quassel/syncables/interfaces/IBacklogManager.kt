package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.protocol.*
import de.kuschku.quasseldroid_ng.protocol.Type

@Syncable(name = "BacklogManager")
interface IBacklogManager : ISyncableObject {
  @Slot
  fun requestBacklog(bufferId: BufferId, first: MsgId = -1, last: MsgId = -1, limit: Int = -1,
                     additional: Int = 0) {
    REQUEST(SLOT, ARG(bufferId, QType.BufferId), ARG(first, QType.MsgId),
            ARG(last, QType.MsgId), ARG(limit, Type.Int), ARG(additional, Type.Int))
  }

  @Slot
  fun requestBacklogAll(first: MsgId = -1, last: MsgId = -1, limit: Int = -1,
                        additional: Int = 0) {
    REQUEST(SLOT, ARG(first, QType.MsgId), ARG(last, QType.MsgId),
            ARG(limit, Type.Int), ARG(additional, Type.Int))
  }

  @Slot
  fun receiveBacklog(bufferId: BufferId, first: MsgId, last: MsgId, limit: Int, additional: Int,
                     messages: QVariantList)

  @Slot
  fun receiveBacklogAll(first: MsgId, last: MsgId, limit: Int, additional: Int,
                        messages: QVariantList)

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
