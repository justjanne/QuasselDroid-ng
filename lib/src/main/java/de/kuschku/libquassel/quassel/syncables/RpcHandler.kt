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

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.quassel.syncables.interfaces.IRpcHandler
import de.kuschku.libquassel.session.BacklogStorage
import de.kuschku.libquassel.session.NotificationManager
import de.kuschku.libquassel.session.Session
import de.kuschku.libquassel.util.helpers.deserializeString
import java.nio.ByteBuffer

class RpcHandler(
  override val session: Session,
  private val backlogStorage: BacklogStorage? = null,
  private val notificationManager: NotificationManager? = null
) : IRpcHandler {
  override fun displayStatusMsg(net: String, msg: String) {
  }

  override fun bufferInfoUpdated(bufferInfo: BufferInfo) {
    session.bufferSyncer.bufferInfoUpdated(bufferInfo)
  }

  override fun identityCreated(identity: QVariantMap) = session.addIdentity(identity)
  override fun identityRemoved(identityId: IdentityId) = session.removeIdentity(identityId)

  override fun networkCreated(networkId: NetworkId) = session.addNetwork(networkId)
  override fun networkRemoved(networkId: NetworkId) = session.removeNetwork(networkId)

  override fun passwordChanged(ignored: Long, success: Boolean) {
  }

  override fun disconnectFromCore() {
    session.disconnectFromCore?.invoke()
  }

  override fun objectRenamed(classname: ByteBuffer, newname: String, oldname: String) {
    session.renameObject(classname.deserializeString(StringSerializer.UTF8) ?: "", newname, oldname)
  }

  override fun displayMsg(message: Message) {
    session.bufferSyncer.bufferInfoUpdated(message.bufferInfo)
    backlogStorage?.storeMessages(session, message)
    notificationManager?.processMessages(session, true, message)
  }

  override fun createIdentity(identity: Identity, additional: QVariantMap) =
    RPC(
      "2createIdentity(Identity,QVariantMap)",
      ARG(identity.toVariantMap(), QType.Identity),
      ARG(additional, Type.QVariantMap)
    )

  override fun removeIdentity(identityId: IdentityId) =
    RPC(
      "2removeIdentity(IdentityId)",
      ARG(identityId, QType.IdentityId)
    )

  override fun createNetwork(networkInfo: INetwork.NetworkInfo, channels: List<String>) =
    RPC(
      "2createNetwork(NetworkInfo,QStringList)",
      ARG(networkInfo.toVariantMap(), QType.NetworkInfo),
      ARG(channels, Type.QStringList)
    )

  override fun removeNetwork(networkId: NetworkId) =
    RPC(
      "2removeNetwork(NetworkId)",
      ARG(networkId, QType.NetworkId)
    )

  override fun changePassword(peerPtr: Long, user: String, old: String, new: String) =
    RPC(
      "2changePassword(PeerPtr,QString,QString,QString)",
      ARG(peerPtr, QType.PeerPtr),
      ARG(user, Type.QString),
      ARG(old, Type.QString),
      ARG(new, Type.QString)
    )

  override fun requestKickClient(id: Int) =
    RPC(
      "2kickClient(int)",
      ARG(id, Type.Int)
    )

  override fun sendInput(bufferInfo: BufferInfo, message: String) =
    RPC(
      "2sendInput(BufferInfo,QString)",
      ARG(bufferInfo, QType.BufferInfo),
      ARG(message, Type.QString)
    )

  inline fun RPC(function: String, vararg arg: QVariant_) {
    // Don’t transmit calls back that we just got from the network
    if (session.shouldRpc(function))
      session.callRpc(function, arg.toList())
  }
}
