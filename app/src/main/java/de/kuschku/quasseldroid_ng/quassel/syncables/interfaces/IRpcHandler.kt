package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import android.net.NetworkInfo
import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.protocol.IdentityId
import de.kuschku.quasseldroid_ng.protocol.NetworkId
import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.QVariant_
import de.kuschku.quasseldroid_ng.quassel.BufferInfo
import de.kuschku.quasseldroid_ng.session.SignalProxy
import java.nio.ByteBuffer

@Syncable(name = "RpcHandler")
interface IRpcHandler {
  val proxy: SignalProxy
  fun RPC(function: String, vararg arg: QVariant_) = proxy.callRpc(function, arg.toList())

  @Slot("__objectRenamed__")
  fun objectRenamed(classname: ByteBuffer, newname: String, oldname: String)

  @Slot("2displayMsg(Message)")
  fun displayMsg(message: QuasselDatabase.RawMessage)

  @Slot("2displayStatusMsg(QString,QString)")
  fun displayStatusMsg(net: String, msg: String)

  @Slot("2bufferInfoUpdated(BufferInfo)")
  fun bufferInfoUpdated(bufferInfo: BufferInfo)

  @Slot("2identityCreated(Identity)")
  fun identityCreated(identity: QVariantMap)

  @Slot("2identityRemoved(IdentityId)")
  fun identityRemoved(identityId: IdentityId)

  @Slot("2networkCreated(NetworkId)")
  fun networkCreated(networkId: NetworkId)

  @Slot("2networkRemoved(NetworkId)")
  fun networkRemoved(networkId: NetworkId)

  @Slot("2passwordChanged(PeerPtr,bool)")
  fun passwordChanged(ignored: Long, success: Boolean)

  @Slot("2disconnectFromCore()")
  fun disconnectFromCore()

  fun requestCreateIdentity(identity: QVariantMap, additional: QVariantMap)
  fun requestRemoveIdentity(identityId: IdentityId)
  fun requestCreateNetwork(networkInfo: NetworkInfo, channels: List<String>)
  fun requestRemoveNetwork(networkId: NetworkId)
  fun requestPasswordChange(peerPtr: Long, user: String, old: String, new: String)
  fun requestKickClient(id: Int)
  fun sendInput(bufferInfo: BufferInfo, message: String)
}
