/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.libquassel.session

import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.Features
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.util.Optional
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.io.Closeable
import javax.net.ssl.SSLSession

interface ISession : Closeable {
  val features: Features
  val sslSession: Observable<Optional<SSLSession>>
  val lag: BehaviorSubject<Long>

  val proxy: SignalProxy

  val aliasManager: AliasManager
  val backlogManager: BacklogManager
  val bufferSyncer: BufferSyncer
  val bufferViewManager: BufferViewManager
  val certManagers: Map<IdentityId, CertManager>
  val coreInfo: CoreInfo
  val dccConfig: DccConfig
  val identities: Map<IdentityId, Identity>
  fun liveIdentities(): Observable<Map<IdentityId, Identity>>
  val ignoreListManager: IgnoreListManager
  val highlightRuleManager: HighlightRuleManager
  val ircListHelper: IrcListHelper
  val networks: Map<NetworkId, Network>
  fun liveNetworks(): Observable<Map<NetworkId, Network>>
  fun liveNetworkAdded(): Observable<NetworkId>
  val networkConfig: NetworkConfig
  val rpcHandler: RpcHandler
  fun network(id: NetworkId): Network?
  fun identity(id: IdentityId): Identity?

  fun login(user: String, pass: String)
  fun setupCore(setupData: HandshakeMessage.CoreSetupData) {
    proxy.dispatch(setupData)
  }

  fun disconnectFromCore()
  fun addNetwork(networkId: NetworkId)
  fun removeNetwork(networkId: NetworkId)
  fun addIdentity(initData: QVariantMap)
  fun removeIdentity(identityId: IdentityId)

  val progress: ProgressData

  data class ProgressData(
    val state: Observable<ConnectionState>,
    val progress: Observable<Pair<Int, Int>>,
    val error: Observable<Error>
  )

  companion object {
    val NULL = object : ISession {
      override val features: Features = Features(
        QuasselFeatures.empty(),
        QuasselFeatures.empty())
      override val sslSession: Observable<Optional<SSLSession>> = Observable.empty()
      override val lag = BehaviorSubject.createDefault(0L)

      override val proxy: SignalProxy = SignalProxy.NULL

      override val rpcHandler = RpcHandler(this)
      override val aliasManager = AliasManager(proxy)
      override val backlogManager = BacklogManager(this)
      override val bufferSyncer = BufferSyncer(this)
      override val bufferViewManager = BufferViewManager(proxy)
      override val certManagers: Map<IdentityId, CertManager> = emptyMap()
      override val coreInfo = CoreInfo(proxy)
      override val dccConfig = DccConfig(proxy)
      override val identities: Map<IdentityId, Identity> = emptyMap()
      override fun liveIdentities() = Observable.empty<Map<IdentityId, Identity>>()
      override val ignoreListManager = IgnoreListManager(this)
      override val highlightRuleManager = HighlightRuleManager(proxy)
      override val ircListHelper = IrcListHelper(proxy)
      override val networks: Map<NetworkId, Network> = emptyMap()
      override fun liveNetworks() = Observable.empty<Map<NetworkId, Network>>()
      override fun liveNetworkAdded(): Observable<NetworkId> = PublishSubject.create()
      override val networkConfig = NetworkConfig(proxy)

      override fun network(id: NetworkId): Network? = null
      override fun identity(id: IdentityId): Identity? = null

      override fun login(user: String, pass: String) = Unit
      override fun setupCore(setupData: HandshakeMessage.CoreSetupData) = Unit
      override fun disconnectFromCore() = Unit
      override fun addNetwork(networkId: NetworkId) = Unit
      override fun removeNetwork(networkId: NetworkId) = Unit
      override fun addIdentity(initData: QVariantMap) = Unit
      override fun removeIdentity(identityId: IdentityId) = Unit

      override val progress = ProgressData(
        state = BehaviorSubject.createDefault(ConnectionState.DISCONNECTED),
        progress = BehaviorSubject.createDefault(Pair(0, 0)),
        error = Observable.empty()
      )

      override fun close() = Unit
    }
  }
}
