package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.protocol.*
import de.kuschku.quasseldroid_ng.protocol.Type

@Syncable(name = "BufferSyncer")
interface IBufferSyncer : ISyncableObject {
  fun initActivities(): QVariantList
  fun initLastSeenMsg(): QVariantList
  fun initMarkerLines(): QVariantList
  fun initSetActivities(data: QVariantList)
  fun initSetLastSeenMsg(data: QVariantList)
  fun initSetMarkerLines(data: QVariantList)

  @Slot
  fun markBufferAsRead(buffer: BufferId) {
    SYNC(SLOT, ARG(buffer, QType.BufferId))
  }

  @Slot
  fun mergeBuffersPermanently(buffer1: BufferId, buffer2: BufferId)

  @Slot
  fun removeBuffer(buffer: BufferId)

  @Slot
  fun renameBuffer(buffer: BufferId, newName: String)

  @Slot
  fun requestMarkBufferAsRead(buffer: BufferId) {
    REQUEST(SLOT, ARG(buffer, QType.BufferId))
  }

  @Slot
  fun requestMergeBuffersPermanently(buffer1: BufferId, buffer2: BufferId) {
    REQUEST(SLOT, ARG(buffer1, QType.BufferId), ARG(buffer2, QType.BufferId))
  }

  @Slot
  fun requestPurgeBufferIds() {
    REQUEST(SLOT)
  }

  @Slot
  fun requestRemoveBuffer(buffer: BufferId) {
    REQUEST(SLOT, ARG(buffer, QType.BufferId))
  }

  @Slot
  fun requestRenameBuffer(buffer: BufferId, newName: String) {
    REQUEST(SLOT, ARG(buffer, QType.BufferId), ARG(newName, Type.QString))
  }

  @Slot
  fun requestSetLastSeenMsg(buffer: BufferId, msgId: MsgId) {
    REQUEST(SLOT, ARG(buffer, QType.BufferId), ARG(msgId, QType.MsgId))
  }

  @Slot
  fun requestSetMarkerLine(buffer: BufferId, msgId: MsgId) {
    REQUEST(SLOT, ARG(buffer, QType.BufferId), ARG(msgId, QType.MsgId))
  }

  @Slot
  fun setBufferActivity(buffer: BufferId, activity: Int) {
    SYNC(SLOT, ARG(buffer, QType.BufferId), ARG(activity, Type.Int))
  }

  @Slot
  fun setLastSeenMsg(buffer: BufferId, msgId: MsgId) {
    SYNC(SLOT, ARG(buffer, QType.BufferId), ARG(msgId, QType.MsgId))
  }

  @Slot
  fun setMarkerLine(buffer: BufferId, msgId: MsgId) {
    SYNC(SLOT, ARG(buffer, QType.BufferId), ARG(msgId, QType.MsgId))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
