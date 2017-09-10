package de.kuschku.quasseldroid_ng.quassel.syncables

import android.net.NetworkInfo
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.protocol.*
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.StringSerializer
import de.kuschku.quasseldroid_ng.protocol.primitive.serializer.deserializeString
import de.kuschku.quasseldroid_ng.quassel.BufferInfo
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.IRpcHandler
import de.kuschku.quasseldroid_ng.session.SignalProxy
import java.nio.ByteBuffer

class RpcHandler(override val proxy: SignalProxy) : IRpcHandler {
  override fun displayStatusMsg(net: String, msg: String) {
  }

  override fun bufferInfoUpdated(bufferInfo: BufferInfo) {
  }

  override fun identityCreated(identity: QVariantMap) {
  }

  override fun identityRemoved(identityId: IdentityId) {
  }

  override fun networkCreated(networkId: NetworkId) {
  }

  override fun networkRemoved(networkId: NetworkId) {
  }

  override fun passwordChanged(ignored: Long, success: Boolean) {
  }

  override fun disconnectFromCore() {
  }

  override fun objectRenamed(classname: ByteBuffer, newname: String, oldname: String) {
    proxy.renameObject(classname.deserializeString(StringSerializer.UTF8) ?: "", newname, oldname)
  }

  override fun displayMsg(message: QuasselDatabase.RawMessage) {
    println(message)
  }

  override fun requestCreateIdentity(identity: QVariantMap, additional: QVariantMap) {
  }

  override fun requestRemoveIdentity(identityId: IdentityId) {
  }

  override fun requestCreateNetwork(networkInfo: NetworkInfo, channels: List<String>) {
  }

  override fun requestRemoveNetwork(networkId: NetworkId) {
  }

  override fun requestPasswordChange(peerPtr: Long, user: String, old: String, new: String) {
  }

  override fun requestKickClient(id: Int) {
    RPC("2requestKickClient(Int)", ARG(id, Type.Int))
  }

  override fun sendInput(bufferInfo: BufferInfo, message: String) {
    RPC("2sendInput(BufferInfo,QString)", ARG(bufferInfo, QType.BufferInfo), ARG(message, Type.QString))
  }
}
