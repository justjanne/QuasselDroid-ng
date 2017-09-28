package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.*
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.io.Closeable

interface ISession : Closeable {
  val state: Observable<ConnectionState>

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

  companion object {
    val NULL = object : ISession {
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

      override fun close() = Unit
      override val state = BehaviorSubject.createDefault(ConnectionState.DISCONNECTED)
    }
  }
}
