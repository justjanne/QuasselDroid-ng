package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.protocol.ARG
import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.SLOT
import de.kuschku.quasseldroid_ng.protocol.Type

@Syncable(name = "CoreInfo")
interface ICoreInfo : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun setCoreData(data: QVariantMap) {
    SYNC(SLOT, ARG(data, Type.QVariantMap))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
