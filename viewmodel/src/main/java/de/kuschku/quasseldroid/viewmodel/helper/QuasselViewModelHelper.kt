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
import javax.net.ssl.SSLPeerUnverifiedException

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
  val peerCertificateChain = sslSession.mapMapNullable {
    try {
      it.peerCertificateChain.mapNotNull(X509Helper::convert)
    } catch (ignored: SSLPeerUnverifiedException) {
      null
    }
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

  /**
   * Builds a buffer list for a given bufferViewConfig
   */
  fun processBufferList(
    bufferViewConfig: Observable<Optional<BufferViewConfig>>,
    filteredTypes: Observable<Pair<Map<BufferId, Int>, Int>>,
    bufferSearch: Observable<String> = Observable.just(""),
    bufferListType: BufferHiddenState = BufferHiddenState.VISIBLE,
    showAllNetworks: Boolean = true
  ): Observable<Pair<BufferViewConfig?, List<BufferProps>>> =
    combineLatest(connectedSession, bufferViewConfig, filteredTypes, bufferSearch)
      .safeSwitchMap { (sessionOptional, configOptional, rawFiltered, bufferSearch) ->
        val session = sessionOptional.orNull()
        val bufferView = configOptional.orNull()
        val (filtered, defaultFiltered) = rawFiltered
        val search = bufferSearch.trim()
        if (session != null && bufferView != null) {
          bufferView.liveUpdates().safeSwitchMap { config ->
            val minimumActivity = config.minimumActivity()

            combineLatest<Collection<BufferId>>(listOf(
              config.liveBuffers(),
              config.liveTemporarilyRemovedBuffers(),
              config.liveRemovedBuffers()
            )).safeSwitchMap { (ids, temp, perm) ->
              val bufferIds = if (search.isNotBlank()) {
                ids + temp + perm
              } else when (bufferListType) {
                BufferHiddenState.VISIBLE          -> ids
                BufferHiddenState.HIDDEN_TEMPORARY -> temp - ids
                BufferHiddenState.HIDDEN_PERMANENT -> perm - temp - ids
              }

              combineLatest(
                session.bufferSyncer.liveBufferInfos(),
                session.liveNetworks()
              ).safeSwitchMap { (bufferInfos, networks) ->
                val prefiltered = bufferIds.asSequence().mapNotNull { id ->
                  bufferInfos[id]
                }.filter {
                  search.isBlank() ||
                  it.type.hasFlag(Buffer_Type.StatusBuffer) ||
                  it.bufferName?.contains(search, ignoreCase = true) == true
                }

                fun transformIds(
                  ids: Sequence<BufferInfo>
                ): Sequence<Observable<BufferProps>> = ids.mapNotNull {
                  val network = networks[it.networkId]
                  if (network == null) {
                    null
                  } else {
                    it to network
                  }
                }.mapNotNull<Pair<BufferInfo, Network>, Observable<BufferProps>> { (info, network) ->
                  session.bufferSyncer.liveActivity(info.bufferId).safeSwitchMap { activity ->
                    session.bufferSyncer.liveHighlightCount(info.bufferId).map { highlights ->
                      Pair(activity, highlights)
                    }
                  }.safeSwitchMap { (rawActivity, highlights) ->
                    val name = info.bufferName?.trim() ?: ""
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
                              matchMode = matchMode
                            )
                          }
                        }
                      }
                      else                                  -> Observable.empty()
                    }
                  }
                }

                /**
                 * Takes a list of buffers and determines if the status buffers for the networks
                 * each buffer belongs to exist, if not, adds pseudo-buffers for them.
                 */
                /**
                 * Takes a list of buffers and determines if the status buffers for the networks
                 * each buffer belongs to exist, if not, adds pseudo-buffers for them.
                 */
                fun missingStatusBuffers(
                  buffers: Sequence<BufferInfo>
                ): Sequence<Observable<BufferProps>> {
                  val totalNetworks =
                    if (showAllNetworks && search.isEmpty()) networks.keys
                    else buffers.filter {
                      !it.type.hasFlag(Buffer_Type.StatusBuffer)
                    }.map {
                      it.networkId
                    }.toList()

                  val availableNetworks = buffers.filter {
                    it.type.hasFlag(Buffer_Type.StatusBuffer)
                  }.map {
                    it.networkId
                  }.toList()

                  val wantedNetworks = if (!config.networkId().isValidId()) totalNetworks
                  else listOf(config.networkId())

                  val missingNetworks = wantedNetworks - availableNetworks

                  return missingNetworks.asSequence().filter {
                    !config.networkId().isValidId() || config.networkId() == it
                  }.filter {
                    config.allowedBufferTypes().hasFlag(Buffer_Type.StatusBuffer)
                  }.mapNotNull {
                    networks[it]
                  }.filter {
                    !config.hideInactiveNetworks() || it.isConnected()
                  }.mapNotNull<Network, Observable<BufferProps>> { network ->
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
                          highlights = 0
                        )
                      }
                    }
                  }
                }

                val buffers = transformIds(prefiltered) + missingStatusBuffers(prefiltered)
                combineLatest(buffers.toList()).map { list ->
                  val wantedNetworks = list.filter {
                    !it.info.type.hasFlag(Buffer_Type.StatusBuffer)
                  }.map {
                    it.info.networkId
                  }.toList()

                  list.asSequence().filter {
                    // Only show the currently selected network
                    !config.networkId().isValidId() ||
                    config.networkId() == it.info.networkId
                  }.filter {
                    // Only show buffers which are allowed, or network status buffers
                    (!config.networkId().isValidId() && it.info.type.hasFlag(Buffer_Type.StatusBuffer)) ||
                    (config.allowedBufferTypes() and it.info.type).isNotEmpty()
                  }.filter {
                    // If we’re searching for buffers, only include the networks with results
                    search.isEmpty() ||
                    it.info.networkId in wantedNetworks
                  }.filter {
                    // If the config is set to hide inactive networks, only show initialized
                    // networks
                    !config.hideInactiveNetworks() ||
                    it.networkConnectionState == INetwork.ConnectionState.Initialized
                  }.filter {
                    // If the config is set to hide inactive buffers, only show ones that are
                    // online or are network status buffers
                    !config.hideInactiveBuffers() ||
                    it.bufferStatus != BufferStatus.OFFLINE ||
                    it.info.type.hasFlag(Buffer_Type.StatusBuffer)
                  }.filter {
                    // Only show buffers which fulfill the minimum activity requirement or are
                    // network status buffers
                    minimumActivity.toInt() <= it.bufferActivity.toInt() ||
                    it.info.type.hasFlag(Buffer_Type.StatusBuffer)
                  }.let {
                    // If the config is set to sort buffers, they are sorted by matchmode, and
                    // within of each match mode, by name (case insensitive)
                    if (config.sortAlphabetically())
                      it.sortedBy { IrcCaseMappers.unicode.toLowerCaseNullable(it.info.bufferName) }
                        .sortedBy { it.matchMode.priority }
                    else it
                  }.sortedBy { props ->
                    !props.info.type.hasFlag(Buffer_Type.StatusBuffer)
                  }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { props ->
                    props.network.networkName
                  }).distinctBy {
                    it.info.bufferId
                  }.toList()
                }.map {
                  Pair<BufferViewConfig?, List<BufferProps>>(config, it)
                }
              }
            }
          }
        } else {
          Observable.just(Pair<BufferViewConfig?, List<BufferProps>>(null, emptyList()))
        }
      }

  /**
   * Prepares a buffer list for display by configuring the current expansion and selection state as
   * well as UI elements
   */
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
      list.asSequence().map { props ->
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
        (props.info.type.hasFlag(BufferInfo.Type.StatusBuffer) || state.networkExpanded)
      }.toList()
    }

  fun processSelectedBuffer(
    bufferViewConfigObservable: Observable<Optional<BufferViewConfig>>,
    selectedBufferId: Observable<BufferId>
  ) = combineLatest(connectedSession, selectedBufferId, bufferViewConfigObservable)
    .safeSwitchMap { (sessionOptional, buffer, bufferViewConfigOptional) ->
      val session = sessionOptional.orNull()
      val bufferViewConfig = bufferViewConfigOptional.orNull()
      val bufferSyncer = session?.bufferSyncer
      if (bufferSyncer != null && bufferViewConfig != null) {
        bufferHiddenState(bufferViewConfig, buffer).safeSwitchMap { bufferHiddenState ->
          session.liveNetworks().safeSwitchMap { networks ->
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
                      hiddenState = bufferHiddenState
                    )
                  } ?: Observable.just(SelectedBufferItem(info))
                }
                Buffer_Type.ChannelBuffer -> {
                  network?.liveIrcChannel(info.bufferName)?.mapNullable(IrcChannel.NULL) {
                    SelectedBufferItem(
                      info,
                      joined = it != null,
                      hiddenState = bufferHiddenState
                    )
                  } ?: Observable.just(SelectedBufferItem(
                    info,
                    hiddenState = bufferHiddenState
                  ))
                }
                else                      ->
                  Observable.just(SelectedBufferItem(
                    info,
                    hiddenState = bufferHiddenState
                  ))
              }
            } else {
              Observable.just(SelectedBufferItem())
            }
          }
        }
      } else {
        Observable.just(SelectedBufferItem())
      }
    }

  fun bufferHiddenState(bufferViewConfig: BufferViewConfig,
                        bufferId: BufferId): Observable<BufferHiddenState> =
    combineLatest(bufferViewConfig.liveBuffers(),
                  bufferViewConfig.liveTemporarilyRemovedBuffers(),
                  bufferViewConfig.liveRemovedBuffers())
      .map { (visible, temp, perm) ->
        when (bufferId) {
          in visible -> BufferHiddenState.VISIBLE
          in temp    -> BufferHiddenState.HIDDEN_TEMPORARY
          in perm    -> BufferHiddenState.HIDDEN_PERMANENT
          else       -> BufferHiddenState.HIDDEN_PERMANENT
        }
      }
}
