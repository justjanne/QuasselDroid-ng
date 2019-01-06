/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
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

package de.kuschku.quasseldroid.viewmodel

import androidx.lifecycle.ViewModel
import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.Features
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.quassel.syncables.*
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.and
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helpers.*
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.viewmodel.data.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class QuasselViewModel : ViewModel() {
  fun resetAccount() {
    bufferViewConfigId.onNext(-1)
    selectedMessages.onNext(emptyMap())
    expandedMessages.onNext(emptySet())
    recentlySentMessages.onNext(emptyList())
    stateReset.onNext(Unit)
  }

  val stateReset = BehaviorSubject.create<Unit>()

  val backendWrapper = BehaviorSubject.createDefault(Observable.empty<Optional<Backend>>())

  val selectedMessages = BehaviorSubject.createDefault(emptyMap<MsgId, FormattedMessage>())
  fun selectedMessagesToggle(key: MsgId, value: FormattedMessage): Int {
    val set = selectedMessages.value.orEmpty()
    val result = if (set.containsKey(key)) set - key else set + Pair(key, value)
    selectedMessages.onNext(result)
    return result.size
  }

  val expandedMessages = BehaviorSubject.createDefault(emptySet<MsgId>())

  val buffer = BehaviorSubject.createDefault(Int.MAX_VALUE)
  val bufferOpened = PublishSubject.create<Unit>()

  val bufferViewConfigId = BehaviorSubject.createDefault(-1)

  val recentlySentMessages = BehaviorSubject.createDefault(emptyList<CharSequence>())
  fun addRecentlySentMessage(message: CharSequence) {
    recentlySentMessages.onNext(
      listOf(message) + recentlySentMessages.value
        .filter { it != message }
        .take(MAX_RECENT_MESSAGES - 1)
    )
  }

  val backend = backendWrapper.switchMap { it }
  val sessionManager = backend.mapMap(Backend::sessionManager)
  val session = sessionManager.mapSwitchMap(SessionManager::session)
  val rpcHandler = session.mapMapNullable(ISession::rpcHandler)
  val features = session.mapMap(ISession::features)
    .mapMap(Features::negotiated)
    .mapOrElse(QuasselFeatures.empty())

  val connectionProgress = sessionManager.mapSwitchMap(SessionManager::connectionProgress)
    .mapOrElse(Triple(ConnectionState.DISCONNECTED, 0, 0))

  val bufferViewManager = session.mapMapNullable(ISession::bufferViewManager)

  val bufferViewConfig = bufferViewManager.flatMapSwitchMap { manager ->
    bufferViewConfigId.map { id ->
      Optional.ofNullable(manager.bufferViewConfig(id))
    }
  }

  val errors = sessionManager.toFlowable(BackpressureStrategy.LATEST).switchMap {
    it.orNull()?.error ?: Flowable.empty()
  }

  val connectionErrors = sessionManager.toFlowable(BackpressureStrategy.LATEST).switchMap {
    it.orNull()?.connectionError ?: Flowable.empty()
  }

  val sslSession = session.flatMapSwitchMap(ISession::sslSession)

  val coreInfo = session.mapMapNullable(ISession::coreInfo).mapSwitchMap(CoreInfo::liveInfo)
  val coreInfoClients = coreInfo.mapMap(CoreInfo.CoreData::sessionConnectedClientData)
    .mapOrElse(emptyList())

  val networkConfig = session.mapMapNullable(ISession::networkConfig)

  val ignoreListManager = session.mapMapNullable(ISession::ignoreListManager)

  val highlightRuleManager = session.mapMapNullable(ISession::highlightRuleManager)

  val aliasManager = session.mapMapNullable(ISession::aliasManager)

  val networks = session.switchMap {
    it.map(ISession::liveNetworks).orElse(Observable.just(emptyMap()))
  }

  val identities = session.switchMap {
    it.map(ISession::liveIdentities).orElse(Observable.just(emptyMap()))
  }

  val bufferSyncer = session.mapMapNullable(ISession::bufferSyncer)
  val allBuffers = bufferSyncer.mapSwitchMap {
    it.liveBufferInfos().map(Map<BufferId, BufferInfo>::values)
  }.mapOrElse(emptyList())

  val network = combineLatest(bufferSyncer, networks, buffer).map { (syncer, networks, buffer) ->
    Optional.ofNullable(syncer.orNull()?.bufferInfo(buffer)?.let { networks[it.networkId] })
  }

  /**
   * An observable of the changes of the markerline, as pairs of `(old, new)`
   */
  val markerLine = session.mapSwitchMap { currentSession ->
    buffer.switchMap { currentBuffer ->
      // Get a stream of the latest marker line
      currentSession.bufferSyncer?.liveMarkerLine(currentBuffer) ?: Observable.empty()
    }
  }

  // Remove orElse
  val lag: Observable<Long> = session.mapSwitchMap(ISession::lag).mapOrElse(0)

  val bufferData = combineLatest(session, buffer).switchMap { (sessionOptional, id) ->
    val session = sessionOptional.orNull()
    val bufferSyncer = session?.bufferSyncer
    if (bufferSyncer != null) {
      session.liveNetworks().switchMap { networks ->
        bufferSyncer.liveBufferInfos().switchMap {
          val info = bufferSyncer.bufferInfo(id)
          val network = networks[info?.networkId]
          if (info == null || network == null) {
            Observable.just(BufferData())
          } else {
            when (info.type.toInt()) {
              BufferInfo.Type.QueryBuffer.toInt()   -> {
                network.liveIrcUser(info.bufferName).switchMap {
                  it.updates().map { user ->
                    BufferData(
                      info = info,
                      network = network,
                      description = user.realName(),
                      ircUser =
                      if (user == IrcUser.NULL) null
                      else user
                    )
                  }
                }
              }
              BufferInfo.Type.ChannelBuffer.toInt() -> {
                network.liveIrcChannel(
                  info.bufferName
                ).switchMap { channel ->
                  channel.updates().map {
                    BufferData(
                      info = info,
                      network = network,
                      description = it.topic(),
                      userCount = it.userCount()
                    )
                  }
                }
              }
              BufferInfo.Type.StatusBuffer.toInt()  -> {
                network.liveConnectionState().map {
                  BufferData(
                    info = info,
                    network = network
                  )
                }
              }
              else                                  -> Observable.just(
                BufferData(
                  description = "type is unknown: ${info.type.toInt()}"
                )
              )
            }
          }
        }
      }
    } else {
      Observable.just(BufferData())
    }
  }
  val bufferDataThrottled = bufferData.distinctUntilChanged().throttleLast(100,
                                                                           TimeUnit.MILLISECONDS)

  val nickData: Observable<List<IrcUserItem>> = combineLatest(session, buffer)
    .switchMap { (sessionOptional, buffer) ->
      val session = sessionOptional.orNull()
      val bufferSyncer = session?.bufferSyncer
      val bufferInfo = bufferSyncer?.bufferInfo(buffer)
      if (bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
        session.liveNetworks().switchMap { networks ->
          val network = networks[bufferInfo.networkId]
          network?.liveIrcChannel(bufferInfo.bufferName)?.switchMapNullable(IrcChannel.NULL) { ircChannel ->
            ircChannel?.liveIrcUsers()?.switchMap { users ->
              combineLatest<IrcUserItem>(
                users.map<IrcUser, Observable<IrcUserItem>?> {
                  it.updates().map { user ->
                    val userModes = ircChannel.userModes(user)
                    val prefixModes = network.prefixModes()

                    val lowestMode = userModes.asSequence().mapNotNull {
                      prefixModes.indexOf(it)
                    }.min() ?: prefixModes.size

                    IrcUserItem(
                      user.nick(),
                      network.modesToPrefixes(userModes),
                      lowestMode,
                      user.realName(),
                      user.hostMask(),
                      user.isAway(),
                      user.network().isMyNick(user.nick()),
                      network.support("CASEMAPPING")
                    )
                  }
                }
              )
            } ?: Observable.just(emptyList())
          }
        }
      } else {
        Observable.just(emptyList())
      }
    }
  val nickDataThrottled = nickData.distinctUntilChanged().throttleLast(100, TimeUnit.MILLISECONDS)

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

  val showHidden = BehaviorSubject.createDefault(false)
  val expandedNetworks = BehaviorSubject.createDefault(emptyMap<NetworkId, Boolean>())
  val selectedBufferId = BehaviorSubject.createDefault(Int.MAX_VALUE)
  val selectedBuffer = combineLatest(session, selectedBufferId, bufferViewConfig)
    .switchMap { (sessionOptional, buffer, bufferViewConfigOptional) ->
      val session = sessionOptional.orNull()
      val bufferSyncer = session?.bufferSyncer
      val bufferViewConfig = bufferViewConfigOptional.orNull()
      if (bufferSyncer != null && bufferViewConfig != null) {
        session.liveNetworks().switchMap { networks ->
          val hiddenState = when {
            bufferViewConfig.removedBuffers().contains(buffer)            ->
              BufferHiddenState.HIDDEN_PERMANENT
            bufferViewConfig.temporarilyRemovedBuffers().contains(buffer) ->
              BufferHiddenState.HIDDEN_TEMPORARY
            else                                                          ->
              BufferHiddenState.VISIBLE
          }

          val info = if (buffer < 0) networks[-buffer]?.let {
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

  val bufferList: Observable<Pair<BufferViewConfig?, List<BufferProps>>> =
    combineLatest(session, bufferViewConfig, showHidden)
      .switchMap { (sessionOptional, configOptional, showHiddenRaw) ->
        val session = sessionOptional.orNull()
        val bufferSyncer = session?.bufferSyncer
        val showHidden = showHiddenRaw ?: false
        val config = configOptional.orNull()
        if (bufferSyncer != null && config != null) {
          session.liveNetworks().switchMap { networks ->
            config.liveUpdates()
              .switchMap { currentConfig ->
                combineLatest<Collection<BufferId>>(
                  listOf(
                    config.liveBuffers(),
                    config.liveTemporarilyRemovedBuffers(),
                    config.liveRemovedBuffers()
                  )
                ).switchMap { (ids, temp, perm) ->
                  fun transformIds(ids: Collection<BufferId>, state: BufferHiddenState) =
                    ids.asSequence().mapNotNull { id ->
                      bufferSyncer.bufferInfo(id)
                    }.filter {
                      currentConfig.networkId() <= 0 || currentConfig.networkId() == it.networkId
                    }.filter {
                      (currentConfig.allowedBufferTypes() and it.type).isNotEmpty() ||
                      (it.type.hasFlag(Buffer_Type.StatusBuffer) && currentConfig.networkId() < 0)
                    }.mapNotNull {
                      val network = networks[it.networkId]
                      if (network == null) {
                        null
                      } else {
                        it to network
                      }
                    }.map<Pair<BufferInfo, Network>, Observable<BufferProps>?> { (info, network) ->
                      bufferSyncer.liveActivity(info.bufferId).switchMap { activity ->
                        bufferSyncer.liveHighlightCount(info.bufferId).map { highlights ->
                          activity to highlights
                        }
                      }.switchMap { (activity, highlights) ->
                        when (info.type.toInt()) {
                          BufferInfo.Type.QueryBuffer.toInt()   -> {
                            network.liveNetworkInfo().switchMap { networkInfo ->
                              network.liveConnectionState().switchMap { connectionState ->
                                network.liveIrcUser(info.bufferName).switchMap {
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
                                      ircUser = user
                                    )
                                  }
                                }
                              }
                            }
                          }
                          BufferInfo.Type.ChannelBuffer.toInt() -> {
                            network.liveNetworkInfo().switchMap { networkInfo ->
                              network.liveConnectionState().switchMap { connectionState ->
                                network.liveIrcChannel(info.bufferName).switchMap { channel ->
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
                                      hiddenState = state
                                    )
                                  }
                                }
                              }
                            }
                          }
                          BufferInfo.Type.StatusBuffer.toInt()  -> {
                            network.liveNetworkInfo().switchMap { networkInfo ->
                              network.liveConnectionState().map { connectionState ->
                                BufferProps(
                                  info = info,
                                  network = networkInfo,
                                  networkConnectionState = connectionState,
                                  bufferStatus = BufferStatus.OFFLINE,
                                  description = "",
                                  activity = activity,
                                  highlights = highlights,
                                  hiddenState = state
                                )
                              }
                            }
                          }
                          else                                  -> Observable.empty()
                        }
                      }
                    }

                  fun missingStatusBuffers(
                    list: Collection<BufferId>): Sequence<Observable<BufferProps>?> {
                    val totalNetworks = networks.keys
                    val wantedNetworks = if (currentConfig.networkId() <= 0) totalNetworks
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
                      currentConfig.networkId() <= 0 || currentConfig.networkId() == it
                    }.filter {
                      currentConfig.allowedBufferTypes().hasFlag(Buffer_Type.StatusBuffer)
                    }.mapNotNull {
                      networks[it]
                    }.filter {
                      !config.hideInactiveNetworks() || it.isConnected()
                    }.map<Network, Observable<BufferProps>?> { network ->
                      network.liveNetworkInfo().switchMap { networkInfo ->
                        network.liveConnectionState().map { connectionState ->
                          BufferProps(
                            info = BufferInfo(
                              bufferId = -networkInfo.networkId,
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

                  bufferSyncer.liveBufferInfos().switchMap {
                    val buffers = if (showHidden) {
                      transformIds(ids, BufferHiddenState.VISIBLE) +
                      transformIds(temp - ids, BufferHiddenState.HIDDEN_TEMPORARY) +
                      transformIds(perm - temp - ids, BufferHiddenState.HIDDEN_PERMANENT) +
                      missingStatusBuffers(ids + temp + perm)
                    } else {
                      transformIds(ids, BufferHiddenState.VISIBLE) +
                      missingStatusBuffers(ids)
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

  companion object {
    const val MAX_RECENT_MESSAGES = 20
  }
}
