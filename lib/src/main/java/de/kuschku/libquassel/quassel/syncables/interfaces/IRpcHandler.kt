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
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.protocol.QuasselType
import de.kuschku.libquassel.protocol.qVariant
import de.kuschku.libquassel.quassel.BufferInfo
import java.nio.ByteBuffer

@SyncedObject(name = "RpcHandler")
interface IRpcHandler : ISyncableObject {
  @SyncedCall(name = "__objectRenamed__", target = ProtocolSide.CLIENT)
  fun objectRenamed(classname: ByteBuffer, newName: String?, oldName: String?) {
    rpc(
      target = ProtocolSide.CLIENT,
      "__objectRenamed__",
      qVariant(classname, QtType.QByteArray),
      qVariant(newName, QtType.QString),
      qVariant(oldName, QtType.QString)
    )
  }

  @SyncedCall(name = "2displayMsg(Message)", target = ProtocolSide.CLIENT)
  fun displayMsg(message: Message) {
    rpc(
      target = ProtocolSide.CLIENT,
      "2displayMsg(Message)",
      qVariant(message, QuasselType.Message)
    )
  }

  @SyncedCall(name = "2displayStatusMsg(QString,QString)", target = ProtocolSide.CLIENT)
  fun displayStatusMsg(net: String?, msg: String?) {
    rpc(
      target = ProtocolSide.CLIENT,
      "2displayStatusMsg(QString,QString)",
      qVariant(net, QtType.QString),
      qVariant(msg, QtType.QString)
    )
  }

  @SyncedCall(name = "2bufferInfoUpdated(BufferInfo)", target = ProtocolSide.CLIENT)
  fun bufferInfoUpdated(bufferInfo: BufferInfo) {
    rpc(
      target = ProtocolSide.CLIENT,
      "2bufferInfoUpdated(BufferInfo)",
      qVariant(bufferInfo, QuasselType.BufferInfo)
    )
  }

  @SyncedCall(name = "2identityCreated(Identity)", target = ProtocolSide.CLIENT)
  fun identityCreated(identity: QVariantMap) {
    rpc(
      target = ProtocolSide.CLIENT,
      "2identityCreated(Identity)",
      qVariant(identity, QuasselType.Identity)
    )
  }

  @SyncedCall(name = "2identityRemoved(IdentityId)", target = ProtocolSide.CLIENT)
  fun identityRemoved(identityId: IdentityId) {
    rpc(
      target = ProtocolSide.CLIENT,
      "2identityRemoved(IdentityId)",
      qVariant(identityId, QuasselType.IdentityId)
    )
  }

  @SyncedCall(name = "2networkCreated(NetworkId)", target = ProtocolSide.CLIENT)
  fun networkCreated(networkId: NetworkId) {
    rpc(
      target = ProtocolSide.CLIENT,
      "2networkCreated(NetworkId)",
      qVariant(networkId, QuasselType.NetworkId)
    )
  }

  @SyncedCall(name = "2networkRemoved(NetworkId)", target = ProtocolSide.CLIENT)
  fun networkRemoved(networkId: NetworkId) {
    rpc(
      target = ProtocolSide.CLIENT,
      "2networkRemoved(NetworkId)",
      qVariant(networkId, QuasselType.NetworkId)
    )
  }

  @SyncedCall(name = "2passwordChanged(PeerPtr,bool)", target = ProtocolSide.CLIENT)
  fun passwordChanged(peer: ULong, success: Boolean) {
    rpc(
      target = ProtocolSide.CLIENT,
      "2passwordChanged(PeerPtr,bool)",
      qVariant(peer, QuasselType.PeerPtr),
      qVariant(success, QtType.Bool)
    )
  }

  @SyncedCall(name = "2disconnectFromCore()", target = ProtocolSide.CLIENT)
  fun disconnectFromCore() {
    rpc(
      target = ProtocolSide.CLIENT,
      "2disconnectFromCore()",
    )
  }

  @SyncedCall(name = "2createIdentity(Identity,QVariantMap)", target = ProtocolSide.CORE)
  fun createIdentity(identity: IIdentity, additional: QVariantMap) {
    rpc(
      target = ProtocolSide.CORE,
      "2createIdentity(Identity,QVariantMap)",
      qVariant(identity, QuasselType.Identity),
      qVariant(additional, QtType.QVariantMap),
    )
  }

  @SyncedCall(name = "2removeIdentity(IdentityId)", target = ProtocolSide.CORE)
  fun removeIdentity(identityId: IdentityId) {
    rpc(
      target = ProtocolSide.CORE,
      "2removeIdentity(IdentityId)",
      qVariant(identityId, QuasselType.IdentityId),
    )
  }

  @SyncedCall(name = "2createNetwork(NetworkInfo,QStringList)", target = ProtocolSide.CORE)
  fun createNetwork(networkInfo: INetwork.NetworkInfo, channels: List<String>) {
    rpc(
      target = ProtocolSide.CORE,
      "2createNetwork(NetworkInfo,QStringList)",
      qVariant(networkInfo, QuasselType.NetworkInfo),
      qVariant(channels, QtType.QStringList),
    )
  }

  @SyncedCall(name = "2removeNetwork(NetworkId)", target = ProtocolSide.CORE)
  fun removeNetwork(networkId: NetworkId) {
    rpc(
      target = ProtocolSide.CORE,
      "2removeNetwork(NetworkId)",
      qVariant(networkId, QuasselType.NetworkId),
    )
  }

  @SyncedCall(name = "2changePassword(PeerPtr,QString,QString,QString)", target = ProtocolSide.CORE)
  fun changePassword(peerPtr: ULong, user: String?, old: String?, new: String?) {
    rpc(
      target = ProtocolSide.CORE,
      "2changePassword(PeerPtr,QString,QString,QString)",
      qVariant(peerPtr, QuasselType.PeerPtr),
      qVariant(user, QtType.QString),
      qVariant(old, QtType.QString),
      qVariant(new, QtType.QString)
    )
  }

  @SyncedCall(name = "2kickClient(int)", target = ProtocolSide.CORE)
  fun requestKickClient(id: Int) {
    rpc(
      target = ProtocolSide.CORE,
      "2kickClient(int)",
      qVariant(id, QtType.Int)
    )
  }

  @SyncedCall(name = "2sendInput(BufferInfo,QString)", target = ProtocolSide.CORE)
  fun sendInput(bufferInfo: BufferInfo, message: String?) {
    rpc(
      target = ProtocolSide.CORE,
      "2sendInput(BufferInfo,QString)",
      qVariant(bufferInfo, QuasselType.BufferInfo),
      qVariant(message, QtType.QString)
    )
  }
}
