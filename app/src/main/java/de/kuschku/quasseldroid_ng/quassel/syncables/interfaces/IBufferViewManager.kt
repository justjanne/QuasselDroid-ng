package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.protocol.*
import de.kuschku.quasseldroid_ng.quassel.syncables.BufferViewConfig

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
    REQUEST(SLOT, ARG(properties, Type.QVariantMap))
  }

  @Slot
  fun requestCreateBufferViews(properties: QVariantList) {
    REQUEST(SLOT, ARG(properties, Type.QVariantList))
  }

  @Slot
  fun requestDeleteBufferView(bufferViewId: Int) {
    REQUEST(SLOT, ARG(bufferViewId, Type.Int))
  }

  @Slot
  fun requestDeleteBufferViews(bufferViews: QVariantList) {
    REQUEST(SLOT, ARG(bufferViews, Type.QVariantList))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
