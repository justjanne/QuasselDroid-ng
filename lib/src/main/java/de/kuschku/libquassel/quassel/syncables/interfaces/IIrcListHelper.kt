/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.quassel.syncables.interfaces

import de.justjanne.libquassel.annotations.ProtocolSide
import de.justjanne.libquassel.annotations.SyncedCall
import de.justjanne.libquassel.annotations.SyncedObject
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QStringList
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.QuasselType
import de.kuschku.libquassel.protocol.qVariant

@SyncedObject("IrcListHelper")
interface IIrcListHelper : ISyncableObject {
  @SyncedCall(target = ProtocolSide.CORE)
  fun requestChannelList(netId: NetworkId, channelFilters: QStringList): QVariantList {
    sync(
      target = ProtocolSide.CORE,
      "requestChannelList",
      qVariant(netId, QuasselType.NetworkId),
      qVariant(channelFilters, QtType.QStringList),
    )
    return emptyList()
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun receiveChannelList(netId: NetworkId, channelFilters: QStringList, channels: QVariantList) {
    sync(
      target = ProtocolSide.CLIENT,
      "receiveChannelList",
      qVariant(netId, QuasselType.NetworkId),
      qVariant(channelFilters, QtType.QStringList),
      qVariant(channels, QtType.QVariantList),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun reportError(error: String?) {
    sync(
      target = ProtocolSide.CLIENT,
      "reportError",
      qVariant(error, QtType.QString),
    )
  }

  @SyncedCall(target = ProtocolSide.CLIENT)
  fun reportFinishedList(netId: NetworkId) {
    sync(
      target = ProtocolSide.CLIENT,
      "reportFinishedList",
      qVariant(netId, QuasselType.NetworkId),
    )
  }
}
