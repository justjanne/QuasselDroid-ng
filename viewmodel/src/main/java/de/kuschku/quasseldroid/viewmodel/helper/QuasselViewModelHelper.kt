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
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.ssl.X509Helper
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.and
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.flag.minus
import de.kuschku.libquassel.util.helper.*
import de.kuschku.libquassel.util.irc.IrcCaseMappers
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

  fun generateBufferProps(
    ids: Collection<BufferId>,
    state: BufferHiddenState,
    bufferSyncer: BufferSyncer,
    networks: Map<NetworkId, Network>,
    currentConfig: BufferViewConfig,
    filtered: Map<BufferId, Int>,
    defaultFiltered: Int,
    bufferSearch: String = ""
  ) =
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
          Pair(activity, highlights)
        }
      }.safeSwitchMap { (rawActivity, highlights) ->
        val name = info.bufferName?.trim() ?: ""
        val search = bufferSearch.trim()
        val matchMode = when {
          name.equals(search, ignoreCase = true)     -> MatchMode.EXACT
          name.startsWith(search, ignoreCase = true) -> MatchMode.START
          else                                       -> MatchMode.CONTAINS
        }
        val activity = rawActivity - Message_Type.of(filtered[info.bufferId]?.toUInt()
                                                     ?: defaultFiltered.toUInt())
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
                      bufferActivity = when {
                        highlights > 0                       ->
                          Buffer_Activity.of(Buffer_Activity.Highlight)
                        activity hasFlag Message_Type.Plain ||
                        activity hasFlag Message_Type.Notice ||
                        activity hasFlag Message_Type.Action ->
                          Buffer_Activity.of(Buffer_Activity.NewMessage)
                        activity.isNotEmpty()                ->
                          Buffer_Activity.of(Buffer_Activity.OtherActivity)
                        else                                 ->
                          Buffer_Activity.of(Buffer_Activity.NoActivity)
                      },
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
                      bufferActivity = when {
                        highlights > 0                       ->
                          Buffer_Activity.of(Buffer_Activity.Highlight)
                        activity hasFlag Message_Type.Plain ||
                        activity hasFlag Message_Type.Notice ||
                        activity hasFlag Message_Type.Action ->
                          Buffer_Activity.of(Buffer_Activity.NewMessage)
                        activity.isNotEmpty()                ->
                          Buffer_Activity.of(Buffer_Activity.OtherActivity)
                        else                                 ->
                          Buffer_Activity.of(Buffer_Activity.NoActivity)
                      },
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
                  bufferActivity = when {
                    highlights > 0                       ->
                      Buffer_Activity.of(Buffer_Activity.Highlight)
                    activity hasFlag Message_Type.Plain ||
                    activity hasFlag Message_Type.Notice ||
                    activity hasFlag Message_Type.Action ->
                      Buffer_Activity.of(Buffer_Activity.NewMessage)
                    activity.isNotEmpty()                ->
                      Buffer_Activity.of(Buffer_Activity.OtherActivity)
                    else                                 ->
                      Buffer_Activity.of(Buffer_Activity.NoActivity)
                  },
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

  fun processRawBufferList(
    bufferViewConfig: Observable<Optional<BufferViewConfig>>,
    filteredTypes: Observable<Pair<Map<BufferId, Int>, Int>>,
    bufferSearch: Observable<String> = Observable.just(""),
    bufferListType: BufferHiddenState = BufferHiddenState.VISIBLE
  ): Observable<Pair<BufferViewConfig?, List<BufferProps>>> =
    combineLatest(connectedSession, bufferViewConfig, filteredTypes, bufferSearch)
      .safeSwitchMap { (sessionOptional, configOptional, rawFiltered, bufferSearch) ->
        val session = sessionOptional.orNull()
        val bufferSyncer = session?.bufferSyncer
        val config = configOptional.orNull()
        val (filtered, defaultFiltered) = rawFiltered
        if (bufferSyncer != null && config != null) {
          session.liveNetworks().safeSwitchMap { networks ->
            config.liveUpdates()
              .safeSwitchMap { currentConfig ->
                combineLatest<Collection<BufferId>>(
                  listOf(
                    config.liveBuffers(),
                    config.liveTemporarilyRemovedBuffers(),
                    config.liveRemovedBuffers()
                  )
                ).safeSwitchMap { (ids, temp, perm) ->
                  fun transformIds(ids: Collection<BufferId>, state: BufferHiddenState) =
                    generateBufferProps(
                      ids,
                      state,
                      bufferSyncer,
                      networks,
                      currentConfig,
                      filtered,
                      defaultFiltered,
                      bufferSearch
                    )

                  fun missingStatusBuffers(
                    list: Collection<BufferId>): Sequence<Observable<BufferProps>?> {
                    val totalNetworks = networks.keys
                    val wantedNetworks = if (!currentConfig.networkId().isValidId()) totalNetworks
                    else listOf(currentConfig.networkId())

                    val availableNetworks = list.asSequence().mapNotNull { id ->
                      bufferSyncer.bufferInfo(id)
                    }.filter {
                      it.type.hasFlag(Buffer_Type.StatusBuffer)
                    }.map {
                      it.networkId
                    }

                    val missingNetworks = wantedNetworks - availableNetworks

                    return missingNetworks.asSequence().filter {
                      !currentConfig.networkId().isValidId() || currentConfig.networkId() == it
                    }.filter {
                      currentConfig.allowedBufferTypes().hasFlag(Buffer_Type.StatusBuffer)
                    }.mapNotNull {
                      networks[it]
                    }.filter {
                      !config.hideInactiveNetworks() || it.isConnected()
                    }.map<Network, Observable<BufferProps>?> { network ->
                      network.liveNetworkInfo().safeSwitchMap { networkInfo ->
                        network.liveConnectionState().map { connectionState ->
                          BufferProps(
                            info = BufferInfo(
                              bufferId = BufferId(-networkInfo.networkId.id),
                              networkId = networkInfo.networkId,
                              groupId = 0,
                              bufferName = networkInfo.networkName,
                              type = Buffer_Type.of(Buffer_Type.StatusBuffer)
                            ),
                            network = networkInfo,
                            networkConnectionState = connectionState,
                            bufferStatus = BufferStatus.OFFLINE,
                            description = "",
                            activity = Message_Type.of(),
                            highlights = 0,
                            hiddenState = BufferHiddenState.VISIBLE
                          )
                        }
                      }
                    }
                  }

                  bufferSyncer.liveBufferInfos().safeSwitchMap {
                    val buffers = if (bufferSearch.isNotBlank()) {
                      transformIds(ids, BufferHiddenState.VISIBLE) +
                      transformIds(temp - ids, BufferHiddenState.HIDDEN_TEMPORARY) +
                      transformIds(perm - temp - ids, BufferHiddenState.HIDDEN_PERMANENT) +
                      missingStatusBuffers(ids + temp + perm)
                    } else when (bufferListType) {
                      BufferHiddenState.VISIBLE          ->
                        transformIds(ids, BufferHiddenState.VISIBLE) +
                        missingStatusBuffers(ids)
                      BufferHiddenState.HIDDEN_TEMPORARY ->
                        transformIds(temp - ids, BufferHiddenState.HIDDEN_TEMPORARY) +
                        missingStatusBuffers(temp - ids)
                      BufferHiddenState.HIDDEN_PERMANENT ->
                        transformIds(perm - temp - ids, BufferHiddenState.HIDDEN_PERMANENT) +
                        missingStatusBuffers(perm - temp - ids)
                    }

                    combineLatest<BufferProps>(buffers.toList()).map { list ->
                      Pair<BufferViewConfig?, List<BufferProps>>(
                        config,
                        list.asSequence().filter {
                          !config.hideInactiveNetworks() ||
                          it.networkConnectionState == INetwork.ConnectionState.Initialized
                        }.filter {
                          (!config.hideInactiveBuffers()) ||
                          it.bufferStatus != BufferStatus.OFFLINE ||
                          it.info.type.hasFlag(Buffer_Type.StatusBuffer)
                        }.let {
                          if (config.sortAlphabetically())
                            it.sortedBy { IrcCaseMappers.unicode.toLowerCaseNullable(it.info.bufferName) }
                              .sortedBy { it.matchMode.priority }
                              .sortedByDescending { it.hiddenState == BufferHiddenState.VISIBLE }
                          else it
                        }.distinctBy {
                          it.info.bufferId
                        }.toList()
                      )
                    }
                  }
                }
              }
          }
        } else {
          Observable.just(Pair<BufferViewConfig?, List<BufferProps>>(null, emptyList()))
        }
      }

  fun filterBufferList(
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
