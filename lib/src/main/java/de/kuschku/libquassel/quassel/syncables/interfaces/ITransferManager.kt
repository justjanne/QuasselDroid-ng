package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.QVariantMap
import java.util.*

@Syncable(name = "TransferManager")
interface ITransferManager : ISyncableObject {
  @Slot
  fun setTransferIds(transferIds: List<UUID>)

  @Slot
  fun onCoreTransferAdded(transferId: UUID)

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
