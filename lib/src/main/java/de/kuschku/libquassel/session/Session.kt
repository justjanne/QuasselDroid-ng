package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.QuasselFeature
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.util.compatibility.HandlerService
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.DEBUG
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.INFO
import de.kuschku.libquassel.util.compatibility.log
import de.kuschku.libquassel.util.hasFlag
import io.reactivex.Flowable
import org.threeten.bp.Instant
import javax.net.ssl.X509TrustManager

class Session(
  val clientData: ClientData,
  val trustManager: X509TrustManager,
  address: SocketAddress,
  handlerService: HandlerService,
  private val userData: Pair<String, String>
) : ProtocolHandler(), ISession {
  var coreFeatures: Quassel_Features = Quassel_Feature.NONE

  private val coreConnection = CoreConnection(this, address, handlerService)
  override val state: Flowable<ConnectionState> = coreConnection.state

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

  init {
    coreConnection.start()
  }

  override fun handle(f: HandshakeMessage.ClientInitAck): Boolean {
    coreFeatures = f.coreFeatures ?: Quassel_Feature.NONE
    dispatch(HandshakeMessage.ClientLogin(
      user = userData.first,
      password = userData.second
    ))
    return true
  }

  override fun handle(f: HandshakeMessage.SessionInit): Boolean {
    coreConnection.setState(ConnectionState.INIT)

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
    coreConnection.setState(ConnectionState.CONNECTED)
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

    aliasManager = null
    backlogManager = null
    bufferSyncer = null
    bufferViewManager = null
    coreInfo = null
    dccConfig = null
    ignoreListManager = null
    ircListHelper = null
    networkConfig = null

    certManagers.clear()
    identities.clear()
    networks.clear()

    super.close()
  }

  fun join() {
    coreConnection.join()
  }
}
