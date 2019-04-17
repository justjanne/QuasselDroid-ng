package de.kuschku.quasseldroid.viewmodel.helper

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.and
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helpers.flatMapSwitchMap
import de.kuschku.libquassel.util.helpers.mapNullable
import de.kuschku.libquassel.util.helpers.mapSwitchMap
import de.kuschku.libquassel.util.helpers.switchMapNullable
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.viewmodel.ChatViewModel
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid.viewmodel.data.*
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open class ChatViewModelHelper @Inject constructor(
  val chat: ChatViewModel,
  quassel: QuasselViewModel
) : QuasselViewModelHelper(quassel) {
  val bufferViewConfig = bufferViewManager.flatMapSwitchMap { manager ->
    chat.bufferViewConfigId.map { id ->
      Optional.ofNullable(manager.bufferViewConfig(id))
    }.mapSwitchMap(BufferViewConfig::liveUpdates)
  }

  val network = combineLatest(bufferSyncer, networks, chat.bufferId)
    .map { (syncer, networks, buffer) ->
      Optional.ofNullable(syncer.orNull()?.bufferInfo(buffer)?.let { networks[it.networkId] })
    }

  /**
   * An observable of the changes of the markerline, as pairs of `(old, new)`
   */
  val markerLine = session.mapSwitchMap { currentSession ->
    chat.bufferId.switchMap { currentBuffer ->
      // Get a stream of the latest marker line
      currentSession.bufferSyncer.liveMarkerLine(currentBuffer)
    }
  }

  val bufferData = combineLatest(session, chat.bufferId)
    .switchMap { (sessionOptional, id) ->
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
  val bufferDataThrottled =
    bufferData.distinctUntilChanged().throttleLast(100, TimeUnit.MILLISECONDS)

  val nickData: Observable<List<IrcUserItem>> = combineLatest(session, chat.bufferId)
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
  val nickDataThrottled =
    nickData.distinctUntilChanged().throttleLast(100, TimeUnit.MILLISECONDS)

  val selectedBuffer = combineLatest(session, chat.selectedBufferId, bufferViewConfig)
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

  val bufferList: Observable<Pair<BufferViewConfig?, List<BufferProps>>> =
    combineLatest(session, bufferViewConfig, chat.showHidden, chat.bufferSearch)
      .switchMap { (sessionOptional, configOptional, showHiddenRaw, bufferSearch) ->
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
                      bufferSyncer.liveActivity(info.bufferId).switchMap { activity ->
                        bufferSyncer.liveHighlightCount(info.bufferId).map { highlights ->
                          activity to highlights
                        }
                      }.switchMap { (activity, highlights) ->
                        val name = info.bufferName?.trim() ?: ""
                        val search = bufferSearch.trim()
                        val matchMode = when {
                          name.equals(search, ignoreCase = true)     ->
                            BufferProps.BufferMatchMode.EXACT
                          name.startsWith(search, ignoreCase = true) ->
                            BufferProps.BufferMatchMode.START
                          else                                       ->
                            BufferProps.BufferMatchMode.CONTAINS
                        }
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
                                      ircUser = user,
                                      matchMode = matchMode
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
                                      hiddenState = state,
                                      matchMode = matchMode
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
                      network.liveNetworkInfo().switchMap { networkInfo ->
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

                  bufferSyncer.liveBufferInfos().switchMap {
                    val buffers = if (showHidden || bufferSearch.isNotBlank()) {
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
}