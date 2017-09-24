package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.QuasselFeature
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.quassel.syncables.interfaces.invokers.Invokers
import de.kuschku.libquassel.util.HandlerService
import de.kuschku.libquassel.util.LoggingHandler.LogLevel.DEBUG
import de.kuschku.libquassel.util.LoggingHandler.LogLevel.INFO
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.libquassel.util.log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.Instant
import javax.net.ssl.X509TrustManager

class Session(
  val clientData: ClientData,
  val trustManager: X509TrustManager
) : ProtocolHandler() {
  var coreFeatures: Quassel_Features = Quassel_Feature.NONE

  var userData: Pair<String, String>? = null

  private var aliasManager: AliasManager? = null
  private var backlogManager: BacklogManager? = null
  private var bufferSyncer: BufferSyncer? = null
  private var bufferViewManager: BufferViewManager? = null
  private var certManagers = mutableMapOf<IdentityId, CertManager>()
  private var coreInfo: CoreInfo? = null
  private var dccConfig: DccConfig? = null
  private var identities = mutableMapOf<IdentityId, Identity>()
  private var ignoreListManager: IgnoreListManager? = null
  private var ircListHelper: IrcListHelper? = null
  private var networks = mutableMapOf<NetworkId, Network>()
  private var networkConfig: NetworkConfig? = null

  private val connection = BehaviorSubject.createDefault(ICoreConnection.NULL)
  val connectionPublisher: Flowable<ICoreConnection> = connection.toFlowable(
    BackpressureStrategy.LATEST)

  init {
    log(INFO, "Session", "Session created")

    // This should preload them
    Invokers
  }

  fun connect(address: SocketAddress, handlerService: HandlerService) {
    val coreConnection = CoreConnection(this, address, handlerService)
    connection.onNext(coreConnection)
    coreConnection.start()
  }

  override fun handle(f: HandshakeMessage.ClientInitAck): Boolean {
    coreFeatures = f.coreFeatures ?: Quassel_Feature.NONE
    dispatch(HandshakeMessage.ClientLogin(
      user = userData?.first,
      password = userData?.second
    ))
    return true
  }

  override fun handle(f: HandshakeMessage.SessionInit): Boolean {
    connection.value.setState(ConnectionState.INIT)

    f.networkIds?.forEach {
      val network = Network(it.value(-1), this)
      networks.put(network.networkId(), network)
    }

    f.identities?.forEach {
      val identity = Identity(this)
      identity.fromVariantMap(it.valueOr(::emptyMap))
      identity.initialized = true
      identities.put(identity.id(), identity)

      val certManager = CertManager(identity.id(), this)
      certManagers.put(identity.id(), certManager)
    }

    isInitializing = true
    networks.values.forEach { syncableObject -> this.synchronize(syncableObject, true) }
    certManagers.values.forEach { syncableObject -> this.synchronize(syncableObject, true) }

    aliasManager = AliasManager(this)
    synchronize(aliasManager, true)

    backlogManager = BacklogManager(this)

    bufferSyncer = BufferSyncer(this)
    synchronize(bufferSyncer, true)

    bufferViewManager = BufferViewManager(this)
    synchronize(bufferViewManager, true)

    coreInfo = CoreInfo(this)
    synchronize(coreInfo, true)

    dccConfig = DccConfig(this)
    if (coreFeatures.hasFlag(QuasselFeature.DccFileTransfer))
      synchronize(dccConfig, true)

    ignoreListManager = IgnoreListManager(this)
    synchronize(ignoreListManager, true)

    ircListHelper = IrcListHelper(this)
    synchronize(ircListHelper, true)

    networkConfig = NetworkConfig(this)
    synchronize(networkConfig, true)

    return true
  }

  override fun onInitDone() {
    connection.value.setState(ConnectionState.CONNECTED)
    log(INFO, "Session", "Initialization finished")
  }

  override fun handle(f: SignalProxyMessage.HeartBeatReply): Boolean {
    val now = Instant.now()
    val latency = now.toEpochMilli() - f.timestamp.toEpochMilli()
    log(INFO, "Session", "Latency of $latency ms")
    return true
  }

  override fun dispatch(message: SignalProxyMessage) {
    log(DEBUG, "Session", "> $message")
    connection.value.dispatch(message)
  }

  override fun dispatch(message: HandshakeMessage) {
    log(DEBUG, "Session", "> $message")
    connection.value.dispatch(message)
  }

  override fun network(id: NetworkId): Network? = networks[id]
  override fun identity(id: IdentityId): Identity? = identities[id]

  override fun cleanUp() {
    connection.value.close()
    connection.onNext(ICoreConnection.NULL)

    aliasManager = null
    backlogManager = null
    bufferSyncer = null
    bufferViewManager = null
    certManagers.clear()
    coreInfo = null
    dccConfig = null
    identities.clear()
    ignoreListManager = null
    ircListHelper = null
    networks.clear()
    networkConfig = null

    super.cleanUp()
  }
}
