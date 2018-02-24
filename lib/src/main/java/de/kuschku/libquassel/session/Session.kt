package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.QuasselFeature
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.util.and
import de.kuschku.libquassel.util.compatibility.HandlerService
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.DEBUG
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.INFO
import de.kuschku.libquassel.util.compatibility.log
import de.kuschku.libquassel.util.hasFlag
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.Instant
import javax.net.ssl.X509TrustManager

class Session(
  val clientData: ClientData,
  val trustManager: X509TrustManager,
  address: SocketAddress,
  handlerService: HandlerService,
  backlogStorage: BacklogStorage,
  private val userData: Pair<String, String>
) : ProtocolHandler(), ISession {
  override var coreFeatures: Quassel_Features = Quassel_Feature.NONE
  override val negotiatedFeatures
    get() = coreFeatures and clientData.clientFeatures

  override val sslSession
    get() = coreConnection.sslSession

  private val coreConnection = CoreConnection(this, address, handlerService)
  override val state = coreConnection.state

  override val aliasManager = AliasManager(this)
  override val backlogManager = BacklogManager(this, backlogStorage)
  override val bufferViewManager = BufferViewManager(this, this)
  override val bufferSyncer = BufferSyncer(this, this)
  override val certManagers = mutableMapOf<IdentityId, CertManager>()
  override val coreInfo = CoreInfo(this)
  override val dccConfig = DccConfig(this)
  override val identities = mutableMapOf<IdentityId, Identity>()
  override val ignoreListManager = IgnoreListManager(this)
  override val ircListHelper = IrcListHelper(this)
  override val networks = mutableMapOf<NetworkId, Network>()
  override val networkConfig = NetworkConfig(this)

  override var rpcHandler: RpcHandler? = RpcHandler(this, backlogStorage)

  override val initStatus = BehaviorSubject.createDefault(0 to 0)

  override val lag = BehaviorSubject.createDefault(0L)

  init {
    coreConnection.start()
  }

  override fun handle(f: HandshakeMessage.ClientInitAck): Boolean {
    coreFeatures = f.coreFeatures ?: Quassel_Feature.NONE
    dispatch(
      HandshakeMessage.ClientLogin(
        user = userData.first,
        password = userData.second
      )
    )

    dispatch(SignalProxyMessage.HeartBeat(Instant.now()))
    return true
  }

  override fun handle(f: HandshakeMessage.SessionInit): Boolean {
    coreConnection.setState(ConnectionState.INIT)

    bufferSyncer.initSetBufferInfos(f.bufferInfos)

    f.networkIds?.forEach {
      val network = Network(it.value(-1), this)
      networks[network.networkId()] = network
    }

    f.identities?.forEach {
      val identity = Identity(this)
      identity.fromVariantMap(it.valueOr(::emptyMap))
      identity.initialized = true
      identities[identity.id()] = identity

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
    if (negotiatedFeatures.hasFlag(QuasselFeature.DccFileTransfer))
      synchronize(dccConfig, true)
    synchronize(ignoreListManager, true)
    synchronize(ircListHelper, true)
    synchronize(networkConfig, true)

    synchronize(backlogManager)

    dispatch(SignalProxyMessage.HeartBeat(Instant.now()))

    return true
  }

  override fun onInitStatusChanged(progress: Int, total: Int) {
    initStatus.onNext(progress to total)
  }

  override fun onInitDone() {
    coreConnection.setState(ConnectionState.CONNECTED)
    log(INFO, "Session", "Initialization finished")

    dispatch(SignalProxyMessage.HeartBeat(Instant.now()))
  }

  override fun handle(f: SignalProxyMessage.HeartBeatReply): Boolean {
    val now = Instant.now()
    val latency = now.toEpochMilli() - f.timestamp.toEpochMilli()
    lag.onNext(latency)
    return true
  }

  override fun dispatch(message: SignalProxyMessage) {
    log(DEBUG, "Session", "> $message")
    coreConnection.dispatch(message)
  }

  override fun dispatch(message: HandshakeMessage) {
    log(DEBUG, "Session", "> $message")
    coreConnection.dispatch(message)
  }

  override fun network(id: NetworkId): Network? = networks[id]
  override fun identity(id: IdentityId): Identity? = identities[id]

  override fun close() {
    coreConnection.close()

    certManagers.clear()
    identities.clear()
    networks.clear()

    super.close()
  }

  fun join() {
    coreConnection.join()
  }
}
