/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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
import de.kuschku.libquassel.protocol.Buffer_Activity
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.ssl.X509Helper
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.and
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helper.*
import de.kuschku.quasseldroid.Backend
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid.viewmodel.data.*
import io.reactivex.Observable
import javax.inject.Inject
import javax.net.ssl.SSLSession

open class QuasselViewModelHelper @Inject constructor(
  val quassel: QuasselViewModel
) {
  val backend = quassel.backendWrapper.safeSwitchMap { it }
  val sessionManager = backend.mapMapNullable(Backend::sessionManager)
  val connectedSession = sessionManager.mapSwitchMap(SessionManager::connectedSession)
  val rpcHandler = connectedSession.mapMap(ISession::rpcHandler)
  val ircListHelper = connectedSession.mapMap(ISession::ircListHelper)
  val features = sessionManager.mapSwitchMap { manager ->
    manager.state.safeSwitchMap { state ->
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

  val errors = sessionManager.safeSwitchMap {
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

  val networks = connectedSession.safeSwitchMap {
    it.map(ISession::liveNetworks).orElse(Observable.just(emptyMap()))
  }

  val identities = connectedSession.safeSwitchMap {
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

  val bufferViewConfigMap = bufferViewManager.safeSwitchMap {
    it.map { manager ->
      manager.liveBufferViewConfigs().map {
        it.mapNotNull(manager::bufferViewConfig).associateBy(BufferViewConfig::bufferViewId)
      }
    }.orElse(Observable.empty())
  }

  fun processRawBufferList(ids: Collection<BufferId>, state: BufferHiddenState,
                           bufferSyncer: BufferSyncer, networks: Map<NetworkId, Network>,
                           currentConfig: BufferViewConfig, bufferSearch: String = "") =
    ids.asSequence().mapNotNull { id ->
      bufferSyncer.bufferInfo(id)
    }.filter {
      bufferSearch.isBlank() ||
      it.type.hasFlag(Buffer_Type.StatusBuffer) ||
      it.bufferName?.contains(bufferSearch, ignoreCase = true) == true
    }.filter {
      !currentConfig.networkId().isValidId() || currentConfig.networkId() == it.networkId
    }.filter {
      (currentConfig.allowedBufferTypes() and it.type).isNotEmpty() ||
      (it.type.hasFlag(Buffer_Type.StatusBuffer) && !currentConfig.networkId().isValidId())
    }.mapNotNull {
      val network = networks[it.networkId]
      if (network == null) {
        null
      } else {
        it to network
      }
    }.map<Pair<BufferInfo, Network>, Observable<BufferProps>?> { (info, network) ->
      bufferSyncer.liveActivity(info.bufferId).safeSwitchMap { activity ->
        bufferSyncer.liveHighlightCount(info.bufferId).map { highlights ->
          activity to highlights
        }
      }.safeSwitchMap { (activity, highlights) ->
        val name = info.bufferName?.trim() ?: ""
        val search = bufferSearch.trim()
        val matchMode = when {
          name.equals(search, ignoreCase = true)     -> MatchMode.EXACT
          name.startsWith(search, ignoreCase = true) -> MatchMode.START
          else                                       -> MatchMode.CONTAINS
        }
        when (info.type.toInt()) {
          BufferInfo.Type.QueryBuffer.toInt()   -> {
            network.liveNetworkInfo().safeSwitchMap { networkInfo ->
              network.liveConnectionState().safeSwitchMap { connectionState ->
                network.liveIrcUser(info.bufferName).safeSwitchMap {
                  it.updates().mapNullable(IrcUser.NULL) { user ->
                    BufferProps(
                      info = info,
                      network = networkInfo,
                      networkConnectionState = connectionState,
                      bufferStatus = when {
                        user == null  -> BufferStatus.OFFLINE
                        user.isAway() -> BufferStatus.AWAY
                        else          -> BufferStatus.ONLINE
                      },
                      description = user?.realName() ?: "",
                      activity = activity,
                      highlights = highlights,
                      hiddenState = state,
                      ircUser = user,
                      matchMode = matchMode
                    )
                  }
                }
              }
            }
          }
          BufferInfo.Type.ChannelBuffer.toInt() -> {
            network.liveNetworkInfo().safeSwitchMap { networkInfo ->
              network.liveConnectionState().safeSwitchMap { connectionState ->
                network.liveIrcChannel(info.bufferName).safeSwitchMap { channel ->
                  channel.updates().mapNullable(IrcChannel.NULL) {
                    BufferProps(
                      info = info,
                      network = networkInfo,
                      networkConnectionState = connectionState,
                      bufferStatus = when (it) {
                        null -> BufferStatus.OFFLINE
                        else -> BufferStatus.ONLINE
                      },
                      description = it?.topic() ?: "",
                      activity = activity,
                      highlights = highlights,
                      hiddenState = state,
                      matchMode = matchMode
                    )
                  }
                }
              }
            }
          }
          BufferInfo.Type.StatusBuffer.toInt()  -> {
            network.liveNetworkInfo().safeSwitchMap { networkInfo ->
              network.liveConnectionState().map { connectionState ->
                BufferProps(
                  info = info,
                  network = networkInfo,
                  networkConnectionState = connectionState,
                  bufferStatus = BufferStatus.OFFLINE,
                  description = "",
                  activity = activity,
                  highlights = highlights,
                  hiddenState = state,
                  matchMode = matchMode
                )
              }
            }
          }
          else                                  -> Observable.empty()
        }
      }
    }

  fun processInternalBufferList(
    buffers: Observable<Pair<BufferViewConfig?, List<BufferProps>>>,
    expandedNetworks: Observable<Map<NetworkId, Boolean>>,
    selected: Observable<BufferId>,
    showHandle: Boolean
  ) =
    combineLatest(
      buffers,
      expandedNetworks,
      selected
    ).map { (info, expandedNetworks, selected) ->
      val (config, list) = info ?: Pair(null, emptyList())
      val minimumActivity = config?.minimumActivity() ?: Buffer_Activity.NONE
      list.asSequence().sortedBy { props ->
        !props.info.type.hasFlag(Buffer_Type.StatusBuffer)
      }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { props ->
        props.network.networkName
      }).map { props ->
        BufferListItem(
          props,
          BufferState(
            networkExpanded = expandedNetworks[props.network.networkId]
                              ?: (props.networkConnectionState == INetwork.ConnectionState.Initialized),
            selected = selected == props.info.bufferId,
            showHandle = showHandle && (config?.sortAlphabetically() == false)
          )
        )
      }.filter { (props, state) ->
        (props.info.type.hasFlag(BufferInfo.Type.StatusBuffer) || state.networkExpanded) &&
        (minimumActivity.toInt() <= props.bufferActivity.toInt() ||
         props.info.type.hasFlag(Buffer_Type.StatusBuffer))
      }.toList()
    }

  fun processSelectedBuffer(
    selectedBufferId: Observable<BufferId>,
    bufferViewConfig: Observable<Optional<BufferViewConfig>>
  ) = combineLatest(connectedSession, selectedBufferId, bufferViewConfig)
    .safeSwitchMap { (sessionOptional, buffer, bufferViewConfigOptional) ->
      val session = sessionOptional.orNull()
      val bufferSyncer = session?.bufferSyncer
      val bufferViewConfig = bufferViewConfigOptional.orNull()
      if (bufferSyncer != null && bufferViewConfig != null) {
        session.liveNetworks().safeSwitchMap { networks ->
          val hiddenState = when {
            bufferViewConfig.removedBuffers().contains(buffer)            ->
              BufferHiddenState.HIDDEN_PERMANENT
            bufferViewConfig.temporarilyRemovedBuffers().contains(buffer) ->
              BufferHiddenState.HIDDEN_TEMPORARY
            else                                                          ->
              BufferHiddenState.VISIBLE
          }

          val info = if (!buffer.isValidId()) networks[NetworkId(-buffer.id)]?.let {
            BufferInfo(
              bufferId = buffer,
              networkId = it.networkId(),
              groupId = 0,
              bufferName = it.networkName(),
              type = Buffer_Type.of(Buffer_Type.StatusBuffer)
            )
          } else bufferSyncer.bufferInfo(buffer)
          if (info != null) {
            val network = networks[info.networkId]
            when (info.type.enabledValues().firstOrNull()) {
              Buffer_Type.StatusBuffer  -> {
                network?.liveConnectionState()?.map {
                  SelectedBufferItem(
                    info,
                    connectionState = it,
                    hiddenState = hiddenState
                  )
                } ?: Observable.just(SelectedBufferItem(info, hiddenState = hiddenState))
              }
              Buffer_Type.ChannelBuffer -> {
                network?.liveIrcChannel(info.bufferName)?.mapNullable(IrcChannel.NULL) {
                  SelectedBufferItem(
                    info,
                    joined = it != null,
                    hiddenState = hiddenState
                  )
                } ?: Observable.just(SelectedBufferItem(info, hiddenState = hiddenState))
              }
              else                      ->
                Observable.just(SelectedBufferItem(info, hiddenState = hiddenState))
            }
          } else {
            Observable.just(SelectedBufferItem())
          }
        }
      } else {
        Observable.just(SelectedBufferItem())
      }
    }
}
