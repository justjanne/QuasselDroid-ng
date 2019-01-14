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

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QStringList
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.quassel.syncables.interfaces.IIrcListHelper
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.rxjava.ReusableUnicastSubject

class IrcListHelper constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "IrcListHelper"), IIrcListHelper {
  sealed class Event {
    data class ChannelList(val netId: NetworkId, val channelFilters: QStringList,
                           val data: QVariantList) : Event()

    data class Finished(val netId: NetworkId) : Event()

    data class Error(val error: String) : Event()
  }

  private val subject = ReusableUnicastSubject.create<Event>()
  val observable = subject.publish().refCount()

  override fun receiveChannelList(netId: NetworkId, channelFilters: QStringList,
                                  data: QVariantList) {
    subject.onNext(Event.ChannelList(netId, channelFilters, data))
  }

  override fun reportFinishedList(netId: NetworkId) {
    subject.onNext(Event.Finished(netId))
  }

  override fun reportError(error: String) {
    subject.onNext(Event.Error(error))
  }
}
