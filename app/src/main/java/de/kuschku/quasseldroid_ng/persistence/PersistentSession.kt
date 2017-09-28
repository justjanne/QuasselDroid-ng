package de.kuschku.quasseldroid_ng.persistence

import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.session.ConnectionState
import de.kuschku.libquassel.session.ISession
import io.reactivex.subjects.BehaviorSubject

class PersistentSession : ISession {
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
