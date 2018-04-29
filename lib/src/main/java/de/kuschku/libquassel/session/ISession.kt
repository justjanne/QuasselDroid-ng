/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
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

import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.Features
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.quassel.syncables.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.io.Closeable
import javax.net.ssl.SSLSession

interface ISession : Closeable {
  val state: Observable<ConnectionState>
  val features: Features
  val sslSession: SSLSession?

  val aliasManager: AliasManager?
  val backlogManager: BacklogManager?
  val bufferSyncer: BufferSyncer?
  val bufferViewManager: BufferViewManager?
  val certManagers: Map<IdentityId, CertManager>
  val coreInfo: CoreInfo?
  val dccConfig: DccConfig?
  val identities: Map<IdentityId, Identity>
  fun liveIdentities(): Observable<Map<IdentityId, Identity>>
  val ignoreListManager: IgnoreListManager?
  val highlightRuleManager: HighlightRuleManager?
  val ircListHelper: IrcListHelper?
  val networks: Map<NetworkId, Network>
  fun liveNetworks(): Observable<Map<NetworkId, Network>>
  val networkConfig: NetworkConfig?
  val rpcHandler: RpcHandler?
  val initStatus: Observable<Pair<Int, Int>>

  val proxy: SignalProxy
  val error: Flowable<Error>
  val lag: Observable<Long>

  fun login(user: String, pass: String)

  companion object {
    val NULL = object : ISession {
      override val proxy: SignalProxy = SignalProxy.NULL
      override val error = BehaviorSubject.create<Error>().toFlowable(BackpressureStrategy.BUFFER)
      override val state = BehaviorSubject.createDefault(ConnectionState.DISCONNECTED)
      override val features: Features = Features(
        QuasselFeatures.empty(),
        QuasselFeatures.empty())
      override val sslSession: SSLSession? = null

      override val rpcHandler: RpcHandler? = null
      override val aliasManager: AliasManager? = null
      override val backlogManager: BacklogManager? = null
      override val bufferSyncer: BufferSyncer? = null
      override val bufferViewManager: BufferViewManager? = null
      override val certManagers: Map<IdentityId, CertManager> = emptyMap()
      override val coreInfo: CoreInfo? = null
      override val dccConfig: DccConfig? = null
      override val identities: Map<IdentityId, Identity> = emptyMap()
      override fun liveIdentities() = Observable.empty<Map<IdentityId, Identity>>()
      override val ignoreListManager: IgnoreListManager? = null
      override val highlightRuleManager: HighlightRuleManager? = null
      override val ircListHelper: IrcListHelper? = null
      override val networks: Map<NetworkId, Network> = emptyMap()
      override fun liveNetworks() = Observable.empty<Map<NetworkId, Network>>()
      override val networkConfig: NetworkConfig? = null
      override val initStatus: Observable<Pair<Int, Int>> = Observable.just(0 to 0)
      override val lag: Observable<Long> = Observable.just(0L)

      override fun login(user: String, pass: String) = Unit
      override fun close() = Unit
    }
  }
}
