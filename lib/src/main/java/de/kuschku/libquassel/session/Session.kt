package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.util.compatibility.HandlerService
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.Instant
import javax.net.ssl.X509TrustManager

class Session(
  clientData: ClientData,
  trustManager: X509TrustManager,
  address: SocketAddress,
  private val handlerService: HandlerService,
  backlogStorage: BacklogStorage,
  private var userData: Pair<String, String>,
  val disconnectFromCore: () -> Unit,
  exceptionHandler: (Throwable) -> Unit
) : ProtocolHandler(exceptionHandler), ISession {
  override val features = Features(clientData.clientFeatures, QuasselFeatures.empty())

  override val sslSession
    get() = coreConnection.sslSession

  private val coreConnection = CoreConnection(
    this, clientData, features, trustManager, address, handlerService
  )
  override val state = coreConnection.state

  private val _error = PublishSubject.create<HandshakeMessage>()
  override val error = _error.toFlowable(BackpressureStrategy.BUFFER)

  override val aliasManager = AliasManager(this)
  override val backlogManager = BacklogManager(this, backlogStorage)
  override val bufferViewManager = BufferViewManager(this, this)
  override val bufferSyncer = BufferSyncer(this, this)
  override val certManagers = mutableMapOf<IdentityId, CertManager>()
  override val coreInfo = CoreInfo(this)
  override val dccConfig = DccConfig(this)

  override val identities = mutableMapOf<IdentityId, Identity>()
  private val live_identities = BehaviorSubject.createDefault(Unit)
  override fun liveIdentities(): Observable<Map<IdentityId, Identity>> = live_identities.map { identities }

  override val ignoreListManager = IgnoreListManager(this)
  override val ircListHelper = IrcListHelper(this)

  override val networks = mutableMapOf<NetworkId, Network>()
  private val live_networks = BehaviorSubject.createDefault(Unit)
  override fun liveNetworks(): Observable<Map<NetworkId, Network>> = live_networks.map { networks }

  override val networkConfig = NetworkConfig(this)

  override var rpcHandler: RpcHandler? = RpcHandler(this, backlogStorage)

  override val initStatus = BehaviorSubject.createDefault(0 to 0)

  override val lag = BehaviorSubject.createDefault(0L)

  init {
    coreConnection.start()
  }

  override fun handle(f: HandshakeMessage.ClientInitAck): Boolean {
    features.core = QuasselFeatures(f.coreFeatures, f.featureList)

    if (f.coreConfigured == true) {
      login()
    } else {
      _error.onNext(f)
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
    _error.onNext(f)
    return true
  }

  override fun handle(f: HandshakeMessage.CoreSetupReject): Boolean {
    _error.onNext(f)
    return true
  }

  override fun handle(f: HandshakeMessage.ClientLoginReject): Boolean {
    _error.onNext(f)
    return true
  }

  fun addNetwork(networkId: NetworkId) {
    val network = Network(networkId, this)
    networks[networkId] = network
    synchronize(network)
    live_networks.onNext(Unit)
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
      synchronize(ircListHelper, true)
      synchronize(networkConfig, true)

      synchronize(backlogManager)

      dispatch(SignalProxyMessage.HeartBeat(Instant.now()))
    }

    return true
  }

  override fun onInitStatusChanged(progress: Int, total: Int) {
    initStatus.onNext(progress to total)
  }

  override fun onInitDone() {
    coreConnection.setState(ConnectionState.CONNECTED)
    dispatch(SignalProxyMessage.HeartBeat(Instant.now()))
  }

  override fun handle(f: SignalProxyMessage.HeartBeatReply): Boolean {
    val now = Instant.now()
    val latency = now.toEpochMilli() - f.timestamp.toEpochMilli()
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

    coreConnection.close()

    certManagers.clear()
    identities.clear()
    networks.clear()
  }

  fun join() {
    coreConnection.join()
  }
}
