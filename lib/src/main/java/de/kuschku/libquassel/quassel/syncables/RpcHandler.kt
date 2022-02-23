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
@file:Suppress("NOTHING_TO_INLINE")

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.IRpcHandler
import de.kuschku.libquassel.session.BacklogStorage
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.NotificationManager
import de.kuschku.libquassel.util.helper.deserializeString
import de.kuschku.libquassel.util.rxjava.ReusableUnicastSubject
import java.nio.ByteBuffer

class RpcHandler(
  var session: ISession,
  private val backlogStorage: BacklogStorage? = null,
  private val notificationManager: NotificationManager? = null
) : SyncableObject(session.proxy, "RpcHandler"), IRpcHandler {
  override fun displayStatusMsg(net: String?, msg: String?) {
  }

  override fun bufferInfoUpdated(bufferInfo: BufferInfo) {
    session.bufferSyncer.bufferInfoUpdated(bufferInfo)
  }

  override fun identityCreated(identity: QVariantMap) = session.addIdentity(identity)
  override fun identityRemoved(identityId: IdentityId) = session.removeIdentity(identityId)

  override fun networkCreated(networkId: NetworkId) = session.addNetwork(networkId)
  override fun networkRemoved(networkId: NetworkId) = session.removeNetwork(networkId)

  private val passwordChangedSubject = ReusableUnicastSubject.create<Boolean>()
  val passwordChanged = passwordChangedSubject.publish().refCount()

  override fun passwordChanged(peer: ULong, success: Boolean) {
    passwordChangedSubject.onNext(success)
  }

  override fun disconnectFromCore() {
    session.disconnectFromCore()
  }

  override fun objectRenamed(classname: ByteBuffer, newName: String?, oldName: String?) {
    session.proxy.renameObject(classname.deserializeString(StringSerializer.UTF8) ?: "",
                               newName ?: "",
                               oldName ?: "")
  }

  override fun displayMsg(message: Message) {
    session.bufferSyncer.bufferInfoUpdated(message.bufferInfo)
    backlogStorage?.storeMessages(session, message)
    notificationManager?.processMessages(session, true, message)
  }
}
