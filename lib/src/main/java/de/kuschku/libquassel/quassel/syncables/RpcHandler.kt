package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.quassel.syncables.interfaces.IRpcHandler
import de.kuschku.libquassel.session.Session
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.helpers.deserializeString
import java.nio.ByteBuffer

class RpcHandler(override val session: Session) : IRpcHandler {
  override fun displayStatusMsg(net: String, msg: String) {
  }

  override fun bufferInfoUpdated(bufferInfo: BufferInfo) {
    session.bufferSyncer.bufferInfoUpdated(bufferInfo)
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
    session.renameObject(classname.deserializeString(StringSerializer.UTF8) ?: "", newname, oldname)
  }

  override fun displayMsg(message: Message) {
    println(message)
  }

  override fun requestCreateIdentity(identity: QVariantMap, additional: QVariantMap) {
  }

  override fun requestRemoveIdentity(identityId: IdentityId) {
  }

  override fun requestCreateNetwork(networkInfo: INetwork.NetworkInfo, channels: List<String>) {
  }

  override fun requestRemoveNetwork(networkId: NetworkId) {
  }

  override fun requestPasswordChange(peerPtr: Long, user: String, old: String, new: String) {
  }

  override fun requestKickClient(id: Int) {
    RPC("2requestKickClient(Int)", ARG(id, Type.Int))
  }

  override fun sendInput(bufferInfo: BufferInfo, message: String) {
    RPC("2sendInput(BufferInfo,QString)", ARG(bufferInfo, QType.BufferInfo),
        ARG(message, Type.QString))
  }

  inline fun RPC(function: String, vararg arg: QVariant_) {
    // Don’t transmit calls back that we just got from the network
    if (session.shouldRpc(function))
      session.callRpc(function, arg.toList())
  }
}
