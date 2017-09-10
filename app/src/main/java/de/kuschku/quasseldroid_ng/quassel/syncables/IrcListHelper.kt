package de.kuschku.quasseldroid_ng.quassel.syncables

import de.kuschku.quasseldroid_ng.protocol.NetworkId
import de.kuschku.quasseldroid_ng.protocol.QStringList
import de.kuschku.quasseldroid_ng.protocol.QVariantList
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.IIrcListHelper
import de.kuschku.quasseldroid_ng.session.SignalProxy

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
