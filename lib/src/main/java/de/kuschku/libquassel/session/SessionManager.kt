package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.ClientData
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.quassel.syncables.interfaces.invokers.Invokers
import de.kuschku.libquassel.util.compatibility.HandlerService
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.libquassel.util.compatibility.log
import de.kuschku.libquassel.util.helpers.or
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.net.ssl.X509TrustManager

class SessionManager(
  private val offlineSession: ISession
) : ISession {
  override val aliasManager: AliasManager?
    get() = session.or(offlineSession).aliasManager
  override val backlogManager: BacklogManager?
    get() = session.or(offlineSession).backlogManager
  override val bufferSyncer: BufferSyncer?
    get() = session.or(offlineSession).bufferSyncer
  override val bufferViewManager: BufferViewManager?
    get() = session.or(offlineSession).bufferViewManager
  override val certManagers: Map<IdentityId, CertManager>
    get() = session.or(offlineSession).certManagers
  override val coreInfo: CoreInfo?
    get() = session.or(offlineSession).coreInfo
  override val dccConfig: DccConfig?
    get() = session.or(offlineSession).dccConfig
  override val identities: Map<IdentityId, Identity>
    get() = session.or(offlineSession).identities
  override val ignoreListManager: IgnoreListManager?
    get() = session.or(offlineSession).ignoreListManager
  override val ircListHelper: IrcListHelper?
    get() = session.or(offlineSession).ircListHelper
  override val networks: Map<NetworkId, Network>
    get() = session.or(offlineSession).networks
  override val networkConfig: NetworkConfig?
    get() = session.or(offlineSession).networkConfig

  override fun close() = session.or(offlineSession).close()

  init {
    log(LoggingHandler.LogLevel.INFO, "Session", "Session created")

    // This should preload them
    Invokers
  }

  fun ifDisconnected(closure: () -> Unit) {
    if (state.or(ConnectionState.DISCONNECTED) == ConnectionState.DISCONNECTED) {
      closure()
    }
  }

  fun connect(
    clientData: ClientData,
    trustManager: X509TrustManager,
    address: SocketAddress,
    handlerService: HandlerService,
    userData: Pair<String, String>
  ) {
    inProgressSession.value.close()
    inProgressSession.onNext(Session(clientData, trustManager, address, handlerService, userData))
  }

  fun disconnect() {
    inProgressSession.value
    inProgressSession.value.close()
    inProgressSession.onNext(offlineSession)
  }

  private var inProgressSession = BehaviorSubject.createDefault(offlineSession)
  override val state: Observable<ConnectionState> = inProgressSession.switchMap { it.state }
  val session: Observable<ISession> = state.map { connectionState ->
    if (connectionState == ConnectionState.CONNECTED)
      inProgressSession.value
    else
      offlineSession
  }
}
