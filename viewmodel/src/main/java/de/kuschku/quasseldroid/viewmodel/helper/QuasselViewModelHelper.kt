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

package de.kuschku.quasseldroid.viewmodel.helper

import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.Features
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.CoreInfo
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.ssl.X509Helper
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helper.*
import de.kuschku.quasseldroid.Backend
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import io.reactivex.Observable
import javax.inject.Inject
import javax.net.ssl.SSLSession

open class QuasselViewModelHelper @Inject constructor(
  val quassel: QuasselViewModel
) {
  val backend = quassel.backendWrapper.switchMap { it }
  val sessionManager = backend.mapMapNullable(Backend::sessionManager)
  val connectedSession = sessionManager.mapSwitchMap(SessionManager::connectedSession)
  val rpcHandler = connectedSession.mapMap(ISession::rpcHandler)
  val ircListHelper = connectedSession.mapMap(ISession::ircListHelper)
  val features = sessionManager.mapSwitchMap { manager ->
    manager.state.switchMap { state ->
      if (state != ConnectionState.CONNECTED) {
        Observable.just(Pair(false, Features.empty()))
      } else {
        manager.connectedSession.map {
          Pair(true, it.features)
        }
      }
    }
  }.mapOrElse(Pair(false, Features.empty()))
  val clientFeatures = features.map { (connected, features) ->
    Pair(connected, features.client)
  }
  val negotiatedFeatures = features.map { (connected, features) ->
    Pair(connected, features.negotiated)
  }
  val coreFeatures = features.map { (connected, features) ->
    Pair(connected, features.core)
  }

  val connectionProgress = sessionManager.mapSwitchMap(SessionManager::connectionProgress)
    .mapOrElse(Triple(ConnectionState.DISCONNECTED, 0, 0))

  val bufferViewManager = connectedSession.mapMap(ISession::bufferViewManager)

  val errors = sessionManager.switchMap {
    it.orNull()?.errors ?: Observable.empty()
  }

  val sslSession = connectedSession.flatMapSwitchMap(ISession::sslSession)
  val peerCertificateChain = sslSession.mapMap(SSLSession::getPeerCertificateChain).mapMap {
    it.mapNotNull(X509Helper::convert)
  }.mapOrElse(emptyList())
  val leafCertificate = peerCertificateChain.map { Optional.ofNullable(it.firstOrNull()) }

  val coreInfo = connectedSession.mapMap(ISession::coreInfo).mapSwitchMap(CoreInfo::liveInfo)
  val coreInfoClients = coreInfo.mapMap(CoreInfo.CoreData::sessionConnectedClientData)
    .mapOrElse(emptyList())

  val networkConfig = connectedSession.mapMap(ISession::networkConfig)

  val ignoreListManager = connectedSession.mapMap(ISession::ignoreListManager)

  val highlightRuleManager = connectedSession.mapMap(ISession::highlightRuleManager)

  val aliasManager = connectedSession.mapMap(ISession::aliasManager)

  val networks = connectedSession.switchMap {
    it.map(ISession::liveNetworks).orElse(Observable.just(emptyMap()))
  }

  val identities = connectedSession.switchMap {
    it.map(ISession::liveIdentities).orElse(Observable.just(emptyMap()))
  }

  val bufferSyncer = connectedSession.mapMap(ISession::bufferSyncer)
  val allBuffers = bufferSyncer.mapSwitchMap {
    it.liveBufferInfos().map(Map<BufferId, BufferInfo>::values)
  }.mapOrElse(emptyList())

  val lag: Observable<Long> = connectedSession.mapSwitchMap(ISession::lag).mapOrElse(0)

  val bufferViewConfigs = bufferViewManager.mapSwitchMap { manager ->
    manager.liveBufferViewConfigs().map { ids ->
      ids.mapNotNull { id ->
        manager.bufferViewConfig(id)
      }.sortedWith(BufferViewConfig.NameComparator)
    }
  }.mapOrElse(emptyList())

  val bufferViewConfigMap = bufferViewManager.switchMap {
    it.map { manager ->
      manager.liveBufferViewConfigs().map {
        it.mapNotNull(manager::bufferViewConfig).associateBy(BufferViewConfig::bufferViewId)
      }
    }.orElse(Observable.empty())
  }
}
