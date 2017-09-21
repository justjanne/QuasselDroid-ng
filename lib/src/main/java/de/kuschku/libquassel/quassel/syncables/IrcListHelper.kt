package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QStringList
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.quassel.syncables.interfaces.IIrcListHelper
import de.kuschku.libquassel.session.SignalProxy

class IrcListHelper constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "IrcListHelper"), IIrcListHelper {
  override fun receiveChannelList(netId: NetworkId, channelFilters: QStringList,
                                  data: QVariantList) {
  }

  override fun reportFinishedList(netId: NetworkId) {
  }

  override fun reportError(error: String) {
  }
}
