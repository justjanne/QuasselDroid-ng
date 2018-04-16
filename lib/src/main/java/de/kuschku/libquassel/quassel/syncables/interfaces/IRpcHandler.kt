package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.session.Session
import java.nio.ByteBuffer

@Syncable(name = "RpcHandler")
interface IRpcHandler {
  val session: Session

  @Slot("__objectRenamed__")
  fun objectRenamed(classname: ByteBuffer, newname: String, oldname: String)

  @Slot("2displayMsg(Message)")
  fun displayMsg(message: Message)

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

  fun createIdentity(identity: Identity, additional: QVariantMap)
  fun removeIdentity(identityId: IdentityId)
  fun createNetwork(networkInfo: INetwork.NetworkInfo, channels: List<String> = emptyList())
  fun removeNetwork(networkId: NetworkId)
  fun changePassword(peerPtr: Long, user: String, old: String, new: String)
  fun requestKickClient(id: Int)
  fun sendInput(bufferInfo: BufferInfo, message: String)
}
