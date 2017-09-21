package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.ARG
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig

@Syncable(name = "BufferViewManager")
interface IBufferViewManager : ISyncableObject {
  fun initBufferViewIds(): QVariantList
  fun initSetBufferViewIds(bufferViewIds: QVariantList)

  fun addBufferViewConfig(config: BufferViewConfig)

  @Slot
  fun addBufferViewConfig(bufferViewConfigId: Int)

  @Slot
  fun deleteBufferViewConfig(bufferViewConfigId: Int)

  @Slot
  fun newBufferViewConfig(bufferViewConfigId: Int) {
    addBufferViewConfig(bufferViewConfigId)
  }

  @Slot
  fun requestCreateBufferView(properties: QVariantMap) {
    REQUEST("requestCreateBufferView", ARG(properties, Type.QVariantMap))
  }

  @Slot
  fun requestCreateBufferViews(properties: QVariantList) {
    REQUEST("requestCreateBufferViews", ARG(properties, Type.QVariantList))
  }

  @Slot
  fun requestDeleteBufferView(bufferViewId: Int) {
    REQUEST("requestDeleteBufferView", ARG(bufferViewId, Type.Int))
  }

  @Slot
  fun requestDeleteBufferViews(bufferViews: QVariantList) {
    REQUEST("requestDeleteBufferViews", ARG(bufferViews, Type.QVariantList))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
