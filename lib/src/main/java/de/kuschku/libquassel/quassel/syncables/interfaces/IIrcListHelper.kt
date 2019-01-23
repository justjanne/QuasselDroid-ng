/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.Type

@Syncable(name = "IrcListHelper")
interface IIrcListHelper : ISyncableObject {
  @Slot
  fun requestChannelList(netId: NetworkId, channelFilters: QStringList): QVariantList {
    REQUEST(
      "requestChannelList", ARG(netId, QType.NetworkId),
      ARG(channelFilters, Type.QStringList)
    )
    return emptyList()
  }

  @Slot
  fun receiveChannelList(netId: NetworkId, channelFilters: QStringList, data: QVariantList)

  @Slot
  fun reportError(error: String?) {
    SYNC("reportError", ARG(error, Type.QString))
  }

  @Slot
  fun reportFinishedList(netId: NetworkId) {
    SYNC("reportFinishedList", ARG(netId, QType.NetworkId))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
