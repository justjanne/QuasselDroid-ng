package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.QuasselFeature
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.quassel.syncables.interfaces.invokers.Invokers
import de.kuschku.libquassel.util.LoggingHandler.LogLevel.DEBUG
import de.kuschku.libquassel.util.LoggingHandler.LogLevel.INFO
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.libquassel.util.log
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.Instant
import javax.net.ssl.X509TrustManager

class Session(
  val clientData: ClientData,
  val trustManager: X509TrustManager
) : ProtocolHandler() {
  var coreFeatures: Quassel_Features = Quassel_Feature.NONE

  var userData: Pair<String, String>? = null

  private val aliasManager = AliasManager(this)
  private val backlogManager = BacklogManager(this)
  private val bufferSyncer = BufferSyncer(this)
  private val bufferViewManager = BufferViewManager(this)
  private val certManagers = mutableMapOf<IdentityId, CertManager>()
  private val coreInfo = CoreInfo(this)
  private val dccConfig = DccConfig(this)
  private val identities = mutableMapOf<IdentityId, Identity>()
  private val ignoreListManager = IgnoreListManager(this)
  private val ircListHelper = IrcListHelper(this)
  private val networks = mutableMapOf<NetworkId, Network>()
  private val networkConfig = NetworkConfig(this)

  val connection = BehaviorSubject.createDefault(ICoreConnection.NULL)

  init {
    log(INFO, "Session", "Session created")

    // This should preload them
    Invokers
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
    connection.value.state.onNext(ConnectionState.INIT)

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
    synchronize(aliasManager, true)
    synchronize(bufferSyncer, true)
    synchronize(bufferViewManager, true)
    synchronize(coreInfo, true)
    if (coreFeatures.hasFlag(QuasselFeature.DccFileTransfer))
      synchronize(dccConfig, true)
    synchronize(ignoreListManager, true)
    synchronize(ircListHelper, true)
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
    super.cleanUp()
  }
}
