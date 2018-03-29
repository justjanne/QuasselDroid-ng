package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.quassel.syncables.interfaces.ISyncableObject

interface SignalProxy {
  fun handle(f: SignalProxyMessage.SyncMessage) = false
  fun handle(f: SignalProxyMessage.RpcCall) = false
  fun handle(f: SignalProxyMessage.InitRequest) = false
  fun handle(f: SignalProxyMessage.InitData) = false
  fun handle(f: SignalProxyMessage.HeartBeat) = false
  fun handle(f: SignalProxyMessage.HeartBeatReply) = false

  fun handle(f: SignalProxyMessage): Boolean = when (f) {
    is SignalProxyMessage.SyncMessage    -> handle(f)
    is SignalProxyMessage.RpcCall        -> handle(f)
    is SignalProxyMessage.InitRequest    -> handle(f)
    is SignalProxyMessage.InitData       -> handle(f)
    is SignalProxyMessage.HeartBeat      -> handle(f)
    is SignalProxyMessage.HeartBeatReply -> handle(f)
  }

  fun dispatch(message: SignalProxyMessage)
  fun dispatch(message: HandshakeMessage)

  fun callSync(type: String, instance: String, slot: String, params: QVariantList)
  fun callRpc(slot: String, params: QVariantList)

  fun shouldSync(type: String, instance: String, slot: String): Boolean
  fun shouldRpc(slot: String): Boolean

  fun network(id: NetworkId): Network?
  fun identity(id: IdentityId): Identity?

  fun renameObject(syncableObject: ISyncableObject, newName: String, oldName: String)
  fun renameObject(className: String, newName: String, oldName: String)
  fun synchronize(syncableObject: ISyncableObject?, baseInit: Boolean)
  fun synchronize(syncableObject: ISyncableObject?) = synchronize(syncableObject, false)
  fun stopSynchronize(syncableObject: ISyncableObject?)

  companion object {
    val NULL = object : SignalProxy {
      override fun dispatch(message: SignalProxyMessage) = Unit
      override fun dispatch(message: HandshakeMessage) = Unit
      override fun callSync(type: String, instance: String, slot: String,
                            params: QVariantList) = Unit

      override fun callRpc(slot: String, params: QVariantList) = Unit
      override fun shouldSync(type: String, instance: String, slot: String) = false
      override fun shouldRpc(slot: String) = false
      override fun network(id: NetworkId): Network? = null
      override fun identity(id: IdentityId): Identity? = null
      override fun renameObject(syncableObject: ISyncableObject, newName: String,
                                oldName: String) = Unit

      override fun renameObject(className: String, newName: String, oldName: String) = Unit
      override fun synchronize(syncableObject: ISyncableObject?, baseInit: Boolean) = Unit
      override fun stopSynchronize(syncableObject: ISyncableObject?) = Unit
    }
  }
}
