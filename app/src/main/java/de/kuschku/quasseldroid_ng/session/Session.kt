package de.kuschku.quasseldroid_ng.session

import de.kuschku.quasseldroid_ng.protocol.*
import de.kuschku.quasseldroid_ng.quassel.QuasselFeature
import de.kuschku.quasseldroid_ng.quassel.syncables.*
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.invokers.Invokers
import de.kuschku.quasseldroid_ng.util.hasFlag
import de.kuschku.quasseldroid_ng.util.helpers.Logger
import de.kuschku.quasseldroid_ng.util.helpers.debug
import org.threeten.bp.Instant
import javax.net.ssl.X509TrustManager

class Session(
  val clientData: ClientData,
  val trustManager: X509TrustManager,
  var coreConnection: CoreConnection? = null
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

  init {
    Logger.debug("Session", "Session created")

    // This should preload them
    Invokers
  }

  override fun handle(function: HandshakeMessage.ClientInitAck) {
    coreFeatures = function.coreFeatures ?: Quassel_Feature.NONE
    dispatch(HandshakeMessage.ClientLogin(
      user = userData?.first,
      password = userData?.second
    ))
  }

  override fun handle(function: HandshakeMessage.ClientLoginReject) {

  }

  override fun handle(function: HandshakeMessage.SessionInit) {
    coreConnection?.state = ConnectionState.INIT

    function.networkIds?.forEach {
      val network = Network(it.value(-1), this)
      networks.put(network.networkId(), network)
    }

    function.identities?.forEach {
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
  }

  override fun onInitDone() {
    coreConnection?.state = ConnectionState.CONNECTED
    Logger.debug("Session", "Initialization finished")
  }

  override fun handle(f: SignalProxyMessage.HeartBeatReply) {
    val now = Instant.now()
    val latency = now.toEpochMilli() - f.timestamp.toEpochMilli()
    Logger.debug("Session", "Latency of $latency ms")
  }

  override fun dispatch(message: SignalProxyMessage) {
    Logger.debug(">", message.toString())
    coreConnection?.dispatch(message)
  }

  override fun dispatch(message: HandshakeMessage) {
    Logger.debug(">", message.toString())
    coreConnection?.dispatch(message)
  }

  override fun network(id: NetworkId): Network? = networks[id]
  override fun identity(id: IdentityId): Identity? = identities[id]
}
