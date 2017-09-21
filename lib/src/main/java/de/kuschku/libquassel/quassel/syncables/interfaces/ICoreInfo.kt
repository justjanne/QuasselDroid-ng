package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.ARG
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.SLOT
import de.kuschku.libquassel.protocol.Type

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
