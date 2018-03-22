package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.ClientData
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.quassel.syncables.interfaces.invokers.Invokers
import de.kuschku.libquassel.util.compatibility.HandlerService
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.helpers.or
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager

class SessionManager(offlineSession: ISession, val backlogStorage: BacklogStorage) : ISession {
  override val features: Features
    get() = session.or(lastSession).features
  override val sslSession: SSLSession?
    get() = session.or(lastSession).sslSession
  override val aliasManager: AliasManager?
    get() = session.or(lastSession).aliasManager
  override val backlogManager: BacklogManager?
    get() = session.or(lastSession).backlogManager
  override val bufferSyncer: BufferSyncer?
    get() = session.or(lastSession).bufferSyncer
  override val bufferViewManager: BufferViewManager?
    get() = session.or(lastSession).bufferViewManager
  override val certManagers: Map<IdentityId, CertManager>
    get() = session.or(lastSession).certManagers
  override val coreInfo: CoreInfo?
    get() = session.or(lastSession).coreInfo
  override val dccConfig: DccConfig?
    get() = session.or(lastSession).dccConfig
  override val identities: Map<IdentityId, Identity>
    get() = session.or(lastSession).identities
  override val ignoreListManager: IgnoreListManager?
    get() = session.or(lastSession).ignoreListManager
  override val ircListHelper: IrcListHelper?
    get() = session.or(lastSession).ircListHelper
  override val networks: Map<NetworkId, Network>
    get() = session.or(lastSession).networks
  override val networkConfig: NetworkConfig?
    get() = session.or(lastSession).networkConfig
  override val rpcHandler: RpcHandler?
    get() = session.or(lastSession).rpcHandler
  override val lag: Observable<Long>
    get() = session.or(lastSession).lag

  override fun close() = session.or(lastSession).close()

  private var lastClientData: ClientData? = null
  private var lastTrustManager: X509TrustManager? = null
  private var lastAddress: SocketAddress? = null
  private var lastHandlerService: (() -> HandlerService)? = null
  private var lastUserData: Pair<String, String>? = null
  private var lastShouldReconnect = false

  private var inProgressSession = BehaviorSubject.createDefault(ISession.NULL)
  private var lastSession: ISession = offlineSession
  override val state: Observable<ConnectionState> = inProgressSession.switchMap { it.state }

  override val initStatus: Observable<Pair<Int, Int>> = inProgressSession.switchMap { it.initStatus }
  val session: Observable<ISession> = state.map { connectionState ->
    if (connectionState == ConnectionState.CONNECTED)
      inProgressSession.value
    else
      lastSession
  }

  val connectionProgress: Observable<Triple<ConnectionState, Int, Int>> = Observable.combineLatest(
    state, initStatus,
    BiFunction<ConnectionState, Pair<Int, Int>, Triple<ConnectionState, Int, Int>> { t1, t2 ->
      Triple(t1, t2.first, t2.second)
    })

  init {
    log(LoggingHandler.LogLevel.INFO, "Session", "Session created")

    state.subscribe {
      if (state == ConnectionState.CONNECTED) {
        lastSession.close()
      }
    }

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
    handlerService: () -> HandlerService,
    userData: Pair<String, String>,
    shouldReconnect: Boolean = false
  ) {
    inProgressSession.value.close()
    lastClientData = clientData
    lastTrustManager = trustManager
    lastAddress = address
    lastHandlerService = handlerService
    lastUserData = userData
    lastShouldReconnect = shouldReconnect
    inProgressSession.onNext(
      Session(
        clientData,
        trustManager,
        address,
        handlerService(),
        backlogStorage,
        userData
      )
    )
  }

  fun reconnect(forceReconnect: Boolean = false) {
    if (lastShouldReconnect || forceReconnect) {
      val clientData = lastClientData
      val trustManager = lastTrustManager
      val address = lastAddress
      val handlerService = lastHandlerService
      val userData = lastUserData

      if (clientData != null && trustManager != null && address != null && handlerService != null && userData != null) {
        if (state.or(
            ConnectionState.DISCONNECTED
          ) == ConnectionState.DISCONNECTED || forceReconnect) {
          connect(clientData, trustManager, address, handlerService, userData, forceReconnect)
        }
      }
    }
  }

  fun disconnect(forever: Boolean) {
    if (forever)
      backlogStorage.clearMessages()
    inProgressSession.value.close()
    inProgressSession.onNext(ISession.NULL)
  }
}
