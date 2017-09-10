package de.kuschku.quasseldroid_ng.session

import de.kuschku.quasseldroid_ng.protocol.*
import de.kuschku.quasseldroid_ng.quassel.syncables.Identity
import de.kuschku.quasseldroid_ng.quassel.syncables.Network
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.ISyncableObject

interface SignalProxy {
  fun handle(f: SignalProxyMessage.SyncMessage) {}
  fun handle(f: SignalProxyMessage.RpcCall) {}
  fun handle(f: SignalProxyMessage.InitRequest) {}
  fun handle(f: SignalProxyMessage.InitData) {}
  fun handle(f: SignalProxyMessage.HeartBeat) {}
  fun handle(f: SignalProxyMessage.HeartBeatReply) {}

  fun handle(f: SignalProxyMessage) = when (f) {
    is SignalProxyMessage.SyncMessage    -> handle(f)
    is SignalProxyMessage.RpcCall        -> handle(f)
    is SignalProxyMessage.InitRequest    -> handle(f)
    is SignalProxyMessage.InitData       -> handle(f)
    is SignalProxyMessage.HeartBeat      -> handle(f)
    is SignalProxyMessage.HeartBeatReply -> handle(f)
  }

  fun dispatch(message: SignalProxyMessage)
  fun dispatch(message: HandshakeMessage)

  fun callSync(type: String, instance: String, slot: String, params: List<QVariant_>)
  fun callRpc(slot: String, params: List<QVariant_>)

  fun network(id: NetworkId): Network?
  fun identity(id: IdentityId): Identity?

  fun renameObject(syncableObject: ISyncableObject, newName: String, oldName: String)
  fun renameObject(className: String, newName: String, oldName: String)
  fun synchronize(syncableObject: ISyncableObject, baseInit: Boolean)
  fun synchronize(syncableObject: ISyncableObject) = synchronize(syncableObject, false)
  fun stopSynchronize(syncableObject: ISyncableObject)
}
