package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.quassel.syncables.*
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.io.Closeable
import javax.net.ssl.SSLSession

interface ISession : Closeable {
  val state: Observable<ConnectionState>
  val features: Features
  val sslSession: SSLSession?

  val aliasManager: AliasManager?
  val backlogManager: BacklogManager?
  val bufferSyncer: BufferSyncer?
  val bufferViewManager: BufferViewManager?
  val certManagers: Map<IdentityId, CertManager>
  val coreInfo: CoreInfo?
  val dccConfig: DccConfig?
  val identities: Map<IdentityId, Identity>
  val ignoreListManager: IgnoreListManager?
  val ircListHelper: IrcListHelper?
  val networks: Map<NetworkId, Network>
  val networkConfig: NetworkConfig?
  val rpcHandler: RpcHandler?
  val initStatus: Observable<Pair<Int, Int>>

  val lag: Observable<Long>

  companion object {
    val NULL = object : ISession {
      override val state = BehaviorSubject.createDefault(ConnectionState.DISCONNECTED)
      override val features: Features = Features(Quassel_Features.of(), Quassel_Features.of())
      override val sslSession: SSLSession? = null

      override val rpcHandler: RpcHandler? = null
      override val aliasManager: AliasManager? = null
      override val backlogManager: BacklogManager? = null
      override val bufferSyncer: BufferSyncer? = null
      override val bufferViewManager: BufferViewManager? = null
      override val certManagers: Map<IdentityId, CertManager> = emptyMap()
      override val coreInfo: CoreInfo? = null
      override val dccConfig: DccConfig? = null
      override val identities: Map<IdentityId, Identity> = emptyMap()
      override val ignoreListManager: IgnoreListManager? = null
      override val ircListHelper: IrcListHelper? = null
      override val networks: Map<NetworkId, Network> = emptyMap()
      override val networkConfig: NetworkConfig? = null
      override val initStatus: Observable<Pair<Int, Int>> = Observable.just(0 to 0)
      override val lag: Observable<Long> = Observable.just(0L)

      override fun close() = Unit
    }
  }
}
