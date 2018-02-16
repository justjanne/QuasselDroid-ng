package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type

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
    SYNC("markBufferAsRead", ARG(buffer, QType.BufferId))
  }

  @Slot
  fun mergeBuffersPermanently(buffer1: BufferId, buffer2: BufferId)

  @Slot
  fun removeBuffer(buffer: BufferId)

  @Slot
  fun renameBuffer(buffer: BufferId, newName: String)

  @Slot
  fun requestMarkBufferAsRead(buffer: BufferId) {
    REQUEST("requestMarkBufferAsRead", ARG(buffer, QType.BufferId))
  }

  @Slot
  fun requestMergeBuffersPermanently(buffer1: BufferId, buffer2: BufferId) {
    REQUEST(
      "requestMergeBuffersPermanently", ARG(buffer1, QType.BufferId),
      ARG(buffer2, QType.BufferId)
    )
  }

  @Slot
  fun requestPurgeBufferIds() {
    REQUEST("requestPurgeBufferIds")
  }

  @Slot
  fun requestRemoveBuffer(buffer: BufferId) {
    REQUEST("requestRemoveBuffer", ARG(buffer, QType.BufferId))
  }

  @Slot
  fun requestRenameBuffer(buffer: BufferId, newName: String) {
    REQUEST("requestRenameBuffer", ARG(buffer, QType.BufferId), ARG(newName, Type.QString))
  }

  @Slot
  fun requestSetLastSeenMsg(buffer: BufferId, msgId: MsgId) {
    REQUEST("requestSetLastSeenMsg", ARG(buffer, QType.BufferId), ARG(msgId, QType.MsgId))
  }

  @Slot
  fun requestSetMarkerLine(buffer: BufferId, msgId: MsgId) {
    REQUEST("requestSetMarkerLine", ARG(buffer, QType.BufferId), ARG(msgId, QType.MsgId))
  }

  @Slot
  fun setBufferActivity(buffer: BufferId, activity: Int) {
    SYNC("setBufferActivity", ARG(buffer, QType.BufferId), ARG(activity, Type.Int))
  }

  @Slot
  fun setLastSeenMsg(buffer: BufferId, msgId: MsgId) {
    SYNC("setLastSeenMsg", ARG(buffer, QType.BufferId), ARG(msgId, QType.MsgId))
  }

  @Slot
  fun setMarkerLine(buffer: BufferId, msgId: MsgId) {
    SYNC("setMarkerLine", ARG(buffer, QType.BufferId), ARG(msgId, QType.MsgId))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
