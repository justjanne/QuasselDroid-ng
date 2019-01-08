/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.session

import de.kuschku.libquassel.connection.*
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.util.compatibility.HandlerService
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.Instant
import javax.net.ssl.X509TrustManager

class Session(
  clientData: ClientData,
  trustManager: X509TrustManager,
  hostnameVerifier: HostnameVerifier,
  address: SocketAddress,
  private val handlerService: HandlerService,
  backlogStorage: BacklogStorage,
  private val notificationManager: NotificationManager?,
  private var userData: Pair<String, String>,
  heartBeatFactory: () -> HeartBeatRunner,
  val disconnectFromCore: (() -> Unit)?,
  private val initCallback: ((Session) -> Unit)?,
  exceptionHandler: (Throwable) -> Unit
) : ProtocolHandler(exceptionHandler), ISession {
  override val objectStorage: ObjectStorage = ObjectStorage(this)
  override val proxy: SignalProxy = this
  override val features = Features(clientData.clientFeatures, QuasselFeatures.empty())

  override val sslSession
    get() = coreConnection.sslSession

  private val coreConnection = CoreConnection(
    clientData,
    features,
    trustManager,
    hostnameVerifier,
    address,
    handlerService
  )
  override val state = coreConnection.state

  private val _error = PublishSubject.create<Error>()
  override val error = _error.toFlowable(BackpressureStrategy.BUFFER)

  private val _connectionError = PublishSubject.create<Throwable>()
  override val connectionError = _connectionError.toFlowable(BackpressureStrategy.LATEST)

  override val aliasManager = AliasManager(this)
  override val backlogManager = BacklogManager(this, backlogStorage)
  override val bufferViewManager = BufferViewManager(this)
  override val bufferSyncer = BufferSyncer(this, notificationManager)
  override val certManagers = mutableMapOf<IdentityId, CertManager>()
  override val coreInfo = CoreInfo(this)
  override val dccConfig = DccConfig(this)

  override val identities = mutableMapOf<IdentityId, Identity>()
  private val live_identities = BehaviorSubject.createDefault(Unit)
  override fun liveIdentities(): Observable<Map<IdentityId, Identity>> = live_identities.map { identities.toMap() }

  override val ignoreListManager = IgnoreListManager(this)
  override val highlightRuleManager = HighlightRuleManager(this)
  override val ircListHelper = IrcListHelper(this)

  override val networks = mutableMapOf<NetworkId, Network>()
  private val live_networks = BehaviorSubject.createDefault(Unit)
  override fun liveNetworks(): Observable<Map<NetworkId, Network>> = live_networks.map { networks.toMap() }

  private val network_added = PublishSubject.create<NetworkId>()
  override fun liveNetworkAdded(): Observable<NetworkId> = network_added

  override val networkConfig = NetworkConfig(this)

  override var rpcHandler: RpcHandler? = RpcHandler(this, backlogStorage, notificationManager)

  override val initStatus = BehaviorSubject.createDefault(0 to 0)

  override val lag = BehaviorSubject.createDefault(0L)

  private val heartBeatThread = heartBeatFactory()

  init {
    heartBeatThread.setCloseCallback(::close)
    heartBeatThread.setHeartbeatDispatchCallback(::dispatch)
    coreConnection.setHandlers(this, ::handle, ::handleConnectionError)
    coreConnection.start()
  }

  override fun handle(f: HandshakeMessage.ClientInitAck): Boolean {
    features.core = QuasselFeatures(f.coreFeatures, f.featureList)

    if (f.coreConfigured == true) {
      login()
    } else {
      _error.onNext(Error.HandshakeError(f))
    }
    return true
  }

  private fun login() {
    dispatch(
      HandshakeMessage.ClientLogin(
        user = userData.first,
        password = userData.second
      )
    )
  }

  override fun login(user: String, pass: String) {
    userData = Pair(user, pass)
    login()
  }

  override fun handle(f: HandshakeMessage.CoreSetupAck): Boolean {
    login()
    return true
  }

  override fun handle(f: HandshakeMessage.ClientInitReject): Boolean {
    _error.onNext(Error.HandshakeError(f))
    return true
  }

  override fun handle(f: HandshakeMessage.CoreSetupReject): Boolean {
    _error.onNext(Error.HandshakeError(f))
    return true
  }

  override fun handle(f: HandshakeMessage.ClientLoginReject): Boolean {
    _error.onNext(Error.HandshakeError(f))
    return true
  }

  fun handle(f: QuasselSecurityException) {
    _error.onNext(Error.SslError(f))
  }

  fun handleConnectionError(f: Throwable) {
    _connectionError.onNext(f)
  }

  fun addNetwork(networkId: NetworkId) {
    val network = Network(networkId, this)
    networks[networkId] = network
    synchronize(network)
    live_networks.onNext(Unit)
    network_added.onNext(networkId)
  }

  fun removeNetwork(networkId: NetworkId) {
    val network = networks.remove(networkId)
    stopSynchronize(network)
    live_networks.onNext(Unit)
  }

  fun addIdentity(initData: QVariantMap) {
    val identity = Identity(this)
    identity.fromVariantMap(initData)
    identities[identity.id()] = identity
    synchronize(identity)
    live_identities.onNext(Unit)
  }

  fun removeIdentity(identityId: IdentityId) {
    val identity = identities.remove(identityId)
    stopSynchronize(identity)
    live_identities.onNext(Unit)
  }

  override fun handle(f: HandshakeMessage.SessionInit): Boolean {
    coreConnection.setState(ConnectionState.INIT)

    handlerService.backend {
      bufferSyncer.initSetBufferInfos(f.bufferInfos)

      f.networkIds?.forEach {
        val network = Network(it.value(-1), this)
        networks[network.networkId()] = network
      }
      live_networks.onNext(Unit)

      f.identities?.forEach {
        val identity = Identity(this)
        identity.fromVariantMap(it.valueOr(::emptyMap))
        identity.initialized = true
        identity.init()
        identities[identity.id()] = identity
        synchronize(identity)

        val certManager = CertManager(identity.id(), this)
        certManagers[identity.id()] = certManager
      }

      isInitializing = true
      networks.values.forEach { syncableObject -> this.synchronize(syncableObject, true) }
      certManagers.values.forEach { syncableObject -> this.synchronize(syncableObject, true) }

      synchronize(aliasManager, true)
      synchronize(bufferSyncer, true)
      synchronize(bufferViewManager, true)
      synchronize(coreInfo, true)
      if (features.negotiated.hasFeature(ExtendedFeature.DccFileTransfer))
        synchronize(dccConfig, true)
      synchronize(ignoreListManager, true)
      if (features.negotiated.hasFeature(ExtendedFeature.CoreSideHighlights))
        synchronize(highlightRuleManager, true)
      synchronize(ircListHelper, true)
      synchronize(networkConfig, true)

      synchronize(backlogManager)
    }

    heartBeatThread.start()

    return true
  }

  override fun onInitStatusChanged(progress: Int, total: Int) {
    initStatus.onNext(progress to total)
  }

  override fun onInitDone() {
    initCallback?.invoke(this)
    for (config in bufferViewManager.bufferViewConfigs()) {
      for (info in bufferSyncer.bufferInfos()) {
        config.handleBuffer(info, bufferSyncer)
      }
    }
    notificationManager?.init(this)
    coreConnection.setState(ConnectionState.CONNECTED)
  }

  override fun handle(f: SignalProxyMessage.HeartBeatReply): Boolean {
    val now = Instant.now()
    heartBeatThread.setLastHeartBeatReply(f.timestamp)
    val latency = now.toEpochMilli() - f.timestamp.toEpochMilli()
    log(LoggingHandler.LogLevel.INFO, "Heartbeat", "Received Heartbeat with ${latency}ms latency")
    lag.onNext(latency)
    return true
  }

  override fun dispatch(message: SignalProxyMessage) {
    if (closed) return
    coreConnection.dispatch(message)
  }

  override fun dispatch(message: HandshakeMessage) {
    if (closed) return
    coreConnection.dispatch(message)
  }

  override fun network(id: NetworkId): Network? = networks[id]
  override fun identity(id: IdentityId): Identity? = identities[id]

  override fun close() {
    super.close()

    heartBeatThread.end()
    coreConnection.close()

    objectStorage.deinit()

    aliasManager.deinit()
    bufferSyncer.deinit()
    bufferViewManager.deinit()
    coreInfo.deinit()
    dccConfig.deinit()
    ignoreListManager.deinit()
    highlightRuleManager.deinit()
    ircListHelper.deinit()
    networkConfig.deinit()
    backlogManager.deinit()

    rpcHandler = null

    certManagers.clear()
    identities.clear()
    live_identities.onNext(Unit)
    networks.clear()
    live_networks.onNext(Unit)
  }

  fun join() {
    coreConnection.join()
  }
}
