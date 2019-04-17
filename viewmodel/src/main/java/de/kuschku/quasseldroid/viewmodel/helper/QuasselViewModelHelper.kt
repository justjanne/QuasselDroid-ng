package de.kuschku.quasseldroid.viewmodel.helper

import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.Features
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.CoreInfo
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.ssl.X509Helper
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helpers.*
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import io.reactivex.Observable
import javax.inject.Inject
import javax.net.ssl.SSLSession

open class QuasselViewModelHelper @Inject constructor(
  val quassel: QuasselViewModel
) {
  val backend = quassel.backendWrapper.switchMap { it }
  val sessionManager = backend.mapMapNullable(Backend::sessionManager)
  val session = sessionManager.mapSwitchMap(SessionManager::session)
  val rpcHandler = session.mapMap(ISession::rpcHandler)
  val ircListHelper = session.mapMap(ISession::ircListHelper)
  val features = sessionManager.mapSwitchMap { manager ->
    manager.state.switchMap { state ->
      if (state != ConnectionState.CONNECTED) {
        Observable.just(Pair(false, Features.empty()))
      } else {
        manager.session.map {
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

  val bufferViewManager = session.mapMap(ISession::bufferViewManager)

  val errors = sessionManager.switchMap {
    it.orNull()?.error ?: Observable.empty()
  }

  val connectionErrors = sessionManager.switchMap {
    it.orNull()?.connectionError ?: Observable.empty()
  }

  val sslSession = session.flatMapSwitchMap(ISession::sslSession)
  val peerCertificateChain = sslSession.mapMap(SSLSession::getPeerCertificateChain).mapMap {
    it.mapNotNull(X509Helper::convert)
  }.mapOrElse(emptyList())
  val leafCertificate = peerCertificateChain.map { Optional.ofNullable(it.firstOrNull()) }

  val coreInfo = session.mapMap(ISession::coreInfo).mapSwitchMap(CoreInfo::liveInfo)
  val coreInfoClients = coreInfo.mapMap(CoreInfo.CoreData::sessionConnectedClientData)
    .mapOrElse(emptyList())

  val networkConfig = session.mapMap(ISession::networkConfig)

  val ignoreListManager = session.mapMap(ISession::ignoreListManager)

  val highlightRuleManager = session.mapMap(ISession::highlightRuleManager)

  val aliasManager = session.mapMap(ISession::aliasManager)

  val networks = session.switchMap {
    it.map(ISession::liveNetworks).orElse(Observable.just(emptyMap()))
  }

  val identities = session.switchMap {
    it.map(ISession::liveIdentities).orElse(Observable.just(emptyMap()))
  }

  val bufferSyncer = session.mapMap(ISession::bufferSyncer)
  val allBuffers = bufferSyncer.mapSwitchMap {
    it.liveBufferInfos().map(Map<BufferId, BufferInfo>::values)
  }.mapOrElse(emptyList())

  val lag: Observable<Long> = session.mapSwitchMap(ISession::lag).mapOrElse(0)

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
