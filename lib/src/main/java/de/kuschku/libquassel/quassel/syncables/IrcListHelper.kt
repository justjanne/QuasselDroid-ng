/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
