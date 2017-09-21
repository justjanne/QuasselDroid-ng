package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type

@Syncable(name = "IrcListHelper")
interface IIrcListHelper : ISyncableObject {
  @Slot
  fun requestChannelList(netId: NetworkId, channelFilters: QStringList): QVariantList {
    REQUEST(SLOT, ARG(netId, QType.NetworkId), ARG(channelFilters, Type.QStringList))
    return emptyList()
  }

  @Slot
  fun receiveChannelList(netId: NetworkId, channelFilters: QStringList, data: QVariantList)

  @Slot
  fun reportError(error: String) {
    SYNC(SLOT, ARG(error, Type.QString))
  }

  @Slot
  fun reportFinishedList(netId: NetworkId) {
    SYNC(SLOT, ARG(netId, QType.NetworkId))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
