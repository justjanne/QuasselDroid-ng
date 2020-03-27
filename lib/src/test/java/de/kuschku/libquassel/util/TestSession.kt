/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.util

import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.Features
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.libquassel.quassel.syncables.interfaces.ISyncableObject
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.ObjectStorage
import de.kuschku.libquassel.session.ProtocolHandler
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertTrue
import javax.net.ssl.SSLSession

class TestSession : ProtocolHandler({ throw it }), ISession {
  private val submissionCallbacks = mutableListOf<SubmissionCallback>()

  interface SubmissionCallback {
    fun callSubmitted(message: SignalProxyMessage)
  }

  class EnsuranceEnvironment(submittedCalls: List<SignalProxyMessage>) {
    private val sync = submittedCalls.mapNotNull { it as? SignalProxyMessage.SyncMessage }
    private val rpc = submittedCalls.mapNotNull { it as? SignalProxyMessage.RpcCall }
    private val initRequest = submittedCalls.mapNotNull { it as? SignalProxyMessage.InitRequest }
    private val initData = submittedCalls.mapNotNull { it as? SignalProxyMessage.InitData }
    private val heartbeat = submittedCalls.mapNotNull { it as? SignalProxyMessage.HeartBeat }
    private val heartbeatReply = submittedCalls.mapNotNull { it as? SignalProxyMessage.HeartBeatReply }

    fun callSync(target: ISyncableObject, slotName: String, params: QVariantList?) {
      val matchingTargetTypes = sync.filter {
        it.className == target.className
      }
      assertTrue(
        "SYNC No calls were made on objects of type ${target.className}",
        matchingTargetTypes.isNotEmpty()
      )
      val matchingTargets = matchingTargetTypes.filter {
        it.objectName == target.objectName
      }
      assertTrue(
        if (matchingTargetTypes.isNotEmpty()) {
          "SYNC No calls were made on ${target.className}:${target.objectName}, instead only on:\n  ${matchingTargetTypes.map { "${it.className}:${it.objectName}" }.joinToString(
            "\n  ")}"
        } else {
          "SYNC No calls were made on ${target.className}:${target.objectName}"
        },
        matchingTargets.isNotEmpty()
      )

      val matchingNames = matchingTargets.filter {
        it.slotName == slotName
      }
      assertTrue(
        "SYNC ${target.className}:${target.objectName}:$slotName() was never called",
        matchingNames.isNotEmpty()
      )
      if (!params.isNullOrEmpty()) {
        val calledParams = matchingNames.map(SignalProxyMessage.SyncMessage::params)
        assertTrue(
          "SYNC ${target.className}:${target.objectName}:$slotName was called with the wrong parameters:\nExpected:\n  $params\nActual:\n  ${calledParams.joinToString(
            "\n  ")}",
          calledParams.contains(params)
        )
      }
    }

    fun callRpc(slotName: String, params: QVariantList?) {
      val matchingNames = rpc.filter {
        it.slotName == slotName
      }
      assertTrue(
        "RPC $slotName() was not called",
        matchingNames.isNotEmpty()
      )
      if (!params.isNullOrEmpty()) {
        val calledParams = matchingNames.map(SignalProxyMessage.RpcCall::params)
        assertTrue(
          "RPC $slotName was called with the wrong parameters:\nExpected:\n  $params\nActual:\n  ${calledParams.joinToString(
            "\n  ")}",
          calledParams.contains(params)
        )
      }
    }

    fun requestInit(className: String, objectName: String) {
      val matchingType = initRequest.filter {
        it.className == className
      }
      assertTrue(
        "InitRequest No data was requested for objects of type $className",
        matchingType.isNotEmpty()
      )
      val matchingCalls = matchingType.filter {
        it.objectName == objectName
      }
      assertTrue(
        "InitRequest No data was requested for object $className:$objectName, instead only for:\n  ${matchingType.map { "${it.className}:${it.objectName}" }.joinToString(
          "\n  ")}",
        matchingCalls.isNotEmpty()
      )
    }
  }

  class TestEnvironment(private val session: TestSession) :
    SubmissionCallback {
    private val submittedCalls = mutableListOf<SignalProxyMessage>()

    fun run(f: ISession.() -> Unit): TestEnvironment {
      session.submissionCallbacks.add(this)
      f.invoke(session)
      session.submissionCallbacks.remove(this)
      return this
    }

    fun does(f: EnsuranceEnvironment.() -> Unit) {
      f.invoke(EnsuranceEnvironment(submittedCalls))
    }

    override fun callSubmitted(message: SignalProxyMessage) {
      submittedCalls.add(message)
    }
  }

  fun ensure(f: ISession.() -> Unit) = TestEnvironment(this).run(f)

  override val proxy = this
  override val objectStorage = ObjectStorage(this)

  override val progress = ISession.ProgressData(
    state = BehaviorSubject.createDefault(ConnectionState.CONNECTED),
    progress = BehaviorSubject.createDefault(Pair(0, 0)),
    error = Observable.empty()
  )

  override val features = Features.all()
  override val sslSession = BehaviorSubject.createDefault(Optional.empty<SSLSession>())

  override val aliasManager = AliasManager(proxy)
  override val backlogManager = BacklogManager(this)
  override val bufferViewManager = BufferViewManager(this)
  override val bufferSyncer = BufferSyncer(this)
  override val certManagers = mutableMapOf<IdentityId, CertManager>()
  override val coreInfo = CoreInfo(this)
  override val dccConfig = DccConfig(this)

  override val identities = mutableMapOf<IdentityId, Identity>()
  private val live_identities = BehaviorSubject.createDefault(Unit)
  override fun liveIdentities(): Observable<Map<IdentityId, Identity>> = live_identities.map { identities.toMap() }
  override fun identity(id: IdentityId) = identities[id]
  override fun addIdentity(initData: QVariantMap) {
    val identity = Identity(this)
    identity.fromVariantMap(initData)
    identities[identity.id()] = identity
    synchronize(identity)
    live_identities.onNext(Unit)
  }

  fun addIdentity(identity: Identity, initialize: Boolean = false) {
    identities[identity.id()] = identity
    if (!initialize)
      identity.initialized = true
    synchronize(identity)
  }

  override fun removeIdentity(identityId: IdentityId) {
    val identity = identities.remove(identityId)
    stopSynchronize(identity)
    live_identities.onNext(Unit)
  }

  override val ignoreListManager = IgnoreListManager(this)
  override val highlightRuleManager = HighlightRuleManager(this)
  override val ircListHelper = IrcListHelper(this)

  override val networks = mutableMapOf<NetworkId, Network>()
  private val live_networks = BehaviorSubject.createDefault(Unit)
  override fun liveNetworks(): Observable<Map<NetworkId, Network>> = live_networks.map { networks.toMap() }
  override fun network(id: NetworkId) = networks[id]
  private val network_added = PublishSubject.create<NetworkId>()
  override fun liveNetworkAdded(): Observable<NetworkId> = network_added
  override fun addNetwork(networkId: NetworkId) {
    val network = Network(networkId, this)
    networks[networkId] = network
    synchronize(network)
    live_networks.onNext(Unit)
    network_added.onNext(networkId)
  }

  fun addNetwork(network: Network, initialize: Boolean = false) {
    networks[network.networkId()] = network
    if (!initialize)
      network.initialized = true
    synchronize(network)
  }

  fun addBufferViewConfig(bufferViewConfig: BufferViewConfig, initialize: Boolean = false) {
    if (!initialize)
      bufferViewConfig.initialized = true
    bufferViewManager.addBufferViewConfig(bufferViewConfig)
    synchronize(bufferViewConfig)
  }

  override fun removeNetwork(networkId: NetworkId) {
    val network = networks.remove(networkId)
    stopSynchronize(network)
    live_networks.onNext(Unit)
  }

  override val networkConfig = NetworkConfig(this)

  override val rpcHandler = RpcHandler(this)

  override val lag = BehaviorSubject.createDefault(0L)

  fun buildNetwork(networkId: NetworkId, f: (Network.() -> Unit)? = null): Network {
    val network = Network(networkId, proxy)
    f?.invoke(network)
    return network
  }

  fun buildIdentity(identityId: IdentityId, f: (Identity.() -> Unit)? = null): Identity {
    val identity = Identity(proxy)
    identity.setId(identityId)
    f?.invoke(identity)
    return identity
  }

  fun buildBufferViewConfig(bufferViewConfigId: Int,
                            f: (BufferViewConfig.() -> Unit)? = null): BufferViewConfig {
    val bufferViewConfig = BufferViewConfig(bufferViewConfigId, proxy)
    f?.invoke(bufferViewConfig)
    return bufferViewConfig
  }

  data class BufferTestData(
    val bufferInfo: BufferInfo,
    val activity: Message_Types = Message_Type.of(),
    val lastSeenMsg: MsgId = MsgId(-1),
    val markerLine: MsgId = MsgId(-1),
    val highlightCount: Int = 0
  )

  data class TestData(
    val session: TestSession,
    var bufferViewConfigs: List<BufferViewConfig> = emptyList(),
    var networks: List<Network> = emptyList(),
    var identities: List<Identity> = emptyList(),
    var buffers: List<BufferTestData> = emptyList(),
    var aliases: List<IAliasManager.Alias> = emptyList()
  ) {
    fun buildBufferViewConfig(bufferViewConfigId: Int,
                              f: (BufferViewConfig.() -> Unit)? = null): BufferViewConfig {
      return session.buildBufferViewConfig(bufferViewConfigId, f)
    }

    fun buildNetwork(networkId: NetworkId, f: (Network.() -> Unit)? = null): Network {
      return session.buildNetwork(networkId, f)
    }

    fun buildIdentity(identityId: IdentityId, f: (Identity.() -> Unit)? = null): Identity {
      return session.buildIdentity(identityId, f)
    }

    fun Network.buildIrcChannel(name: String, f: IrcChannel.() -> Unit): IrcChannel {
      val ircChannel = IrcChannel(name, this, session.proxy)
      ircChannel.initialized = true
      f.invoke(ircChannel)
      newIrcChannel(name, ircChannel.toVariantMap())
      return ircChannel(name)!!
    }

    fun Network.buildIrcUser(name: String, f: IrcUser.() -> Unit): IrcUser {
      val ircUser = IrcUser(name, this, session.proxy)
      ircUser.initialized = true
      f.invoke(ircUser)
      newIrcUser(name, ircUser.toVariantMap())
      return ircUser(name)!!
    }
  }

  fun provideTestData(f: TestData.() -> Unit): TestSession {
    val data = TestData(this)
    f.invoke(data)
    for (bufferViewConfig in data.bufferViewConfigs) {
      addBufferViewConfig(bufferViewConfig)
    }
    for (network in data.networks) {
      addNetwork(network)
    }
    for (identity in data.identities) {
      addIdentity(identity)
    }
    bufferSyncer.setBufferInfos(data.buffers.map {
      it.bufferInfo
    })
    bufferSyncer.setActivities(data.buffers.map {
      Pair(it.bufferInfo.bufferId, it.activity)
    })
    bufferSyncer.setMarkerLines(data.buffers.map {
      Pair(it.bufferInfo.bufferId, it.markerLine)
    })
    bufferSyncer.setLastSeenMsg(data.buffers.map {
      Pair(it.bufferInfo.bufferId, it.lastSeenMsg)
    })
    bufferSyncer.setHighlightCounts(data.buffers.map {
      Pair(it.bufferInfo.bufferId, it.highlightCount)
    })
    aliasManager.setAliasList(data.aliases)
    return this
  }

  override fun onInitDone() = Unit
  override fun dispatch(message: HandshakeMessage) = Unit
  override fun dispatch(message: SignalProxyMessage) {
    for (submissionCallback in submissionCallbacks) {
      submissionCallback.callSubmitted(message)
    }
  }

  override fun login(user: String, pass: String) = Unit

  override fun setupCore(setupData: HandshakeMessage.CoreSetupData) {
    dispatch(setupData)
  }

  override fun disconnectFromCore() {}
}
