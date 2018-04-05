package de.kuschku.quasseldroid.viewmodel

import android.arch.lifecycle.ViewModel
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.session.ConnectionState
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SessionManager
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.and
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helpers.*
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.switchMapNotNull
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.viewmodel.data.*
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class QuasselViewModel : ViewModel() {
  val backendWrapper = BehaviorSubject.createDefault(Observable.empty<Optional<Backend>>())

  val selectedMessages = BehaviorSubject.createDefault(emptyMap<MsgId, FormattedMessage>())
  fun selectedMessagesToggle(key: MsgId, value: FormattedMessage): Boolean {
    val set = selectedMessages.value.orEmpty()
    val result = if (set.containsKey(key)) set - key else set + Pair(key, value)
    selectedMessages.onNext(result)
    return result.isNotEmpty()
  }

  val expandedMessages = BehaviorSubject.createDefault(emptySet<MsgId>())

  val buffer = BehaviorSubject.createDefault(-1)
  val buffer_liveData = buffer.toLiveData()

  val bufferViewConfigId = BehaviorSubject.createDefault(-1)

  val MAX_RECENT_MESSAGES = 20
  val recentlySentMessages = BehaviorSubject.createDefault(emptyList<CharSequence>())
  val recentlySentMessages_liveData = recentlySentMessages.toLiveData()
  fun addRecentlySentMessage(message: CharSequence) {
    recentlySentMessages.onNext(
      listOf(message) + recentlySentMessages.value
        .filter { it != message }
        .take(MAX_RECENT_MESSAGES - 1)
    )
  }

  val backend = backendWrapper.switchMap { it }
  val sessionManager = backend.mapMap(Backend::sessionManager)
  val sessionManager_liveData = sessionManager.toLiveData()
  val session = sessionManager.mapSwitchMap(SessionManager::session)

  val connectionProgress = sessionManager.mapSwitchMap(SessionManager::connectionProgress)
    .mapOrElse(Triple(ConnectionState.DISCONNECTED, 0, 0))
  val connectionProgress_liveData = connectionProgress.toLiveData()

  val bufferViewManager = session.mapMapNullable(ISession::bufferViewManager)

  val bufferViewConfig = bufferViewManager.flatMapSwitchMap { manager ->
    bufferViewConfigId.map { id ->
      Optional.ofNullable(manager.bufferViewConfig(id))
    }
  }

  val errors = sessionManager.toLiveData().switchMapNotNull {
    it.orNull()?.error?.toLiveData()
  }

  val networkConfig = session.map {
    it.map(ISession::networkConfig)
  }

  val networks = session.switchMap {
    it.map(ISession::liveNetworks).orElse(Observable.just(emptyMap()))
  }

  val identities = session.switchMap {
    it.map(ISession::liveIdentities).orElse(Observable.just(emptyMap()))
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
  val markerLine_liveData = markerLine.toLiveData()

  // Remove orElse
  val lag: Observable<Long> = session.mapSwitchMap(ISession::lag).mapOrElse(0)

  val isSecure: Observable<Boolean> = session.mapSwitchMap { session ->
    session.state.map { _ ->
      session.sslSession != null
    }
  }.mapOrElse(false)

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
                      description = user.realName()
                    )
                  }
                }
              }
              BufferInfo.Type.ChannelBuffer.toInt() -> {
                network.liveIrcChannel(
                  info.bufferName
                ).switchMap { channel ->
                  channel.liveUpdates().map {
                    BufferData(
                      info = info,
                      network = network,
                      description = it.topic()
                    )
                  }
                }
              }
              BufferInfo.Type.StatusBuffer.toInt()  -> {
                network.live_connectionState.map {
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

  val nickData: Observable<List<IrcUserItem>> = combineLatest(session, buffer)
    .switchMap { (sessionOptional, buffer) ->
      val session = sessionOptional.orNull()
      val bufferSyncer = session?.bufferSyncer
      val bufferInfo = bufferSyncer?.bufferInfo(buffer)
      if (bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
        session.liveNetworks().switchMap { networks ->
          val network = networks[bufferInfo.networkId]
          val ircChannel = network?.ircChannel(bufferInfo.bufferName)
          if (ircChannel != null) {
            ircChannel.liveIrcUsers().switchMap { users ->
              combineLatest<IrcUserItem>(
                users.map<IrcUser, Observable<IrcUserItem>?> {
                  it.updates().map { user ->
                    val userModes = ircChannel.userModes(user)
                    val prefixModes = network.prefixModes()

                    val lowestMode = userModes.mapNotNull {
                      prefixModes.indexOf(it)
                    }.min() ?: prefixModes.size

                    IrcUserItem(
                      user.nick(),
                      network.modesToPrefixes(userModes),
                      lowestMode,
                      user.realName(),
                      user.isAway(),
                      network.support("CASEMAPPING"),
                      Regex("[us]id(\\d+)").matchEntire(user.user())?.groupValues?.lastOrNull()?.let {
                        "https://www.irccloud.com/avatar-redirect/$it"
                      }
                    )
                  }
                }
              )
            }
          } else {
            Observable.just(emptyList())
          }
        }
      } else {
        Observable.just(emptyList())
      }
    }

  val lastWord = BehaviorSubject.create<Observable<Pair<String, IntRange>>>()

  val rawAutoCompleteData: Observable<Triple<Optional<ISession>, Int, Pair<String, IntRange>>> =
    combineLatest(session, buffer, lastWord).switchMap { (sessionOptional, id, lastWordWrapper) ->
      lastWordWrapper
        .distinctUntilChanged()
        .map { lastWord ->
          Triple(sessionOptional, id, lastWord)
        }
    }

  var time = 0L
  var previous: Any? = null
  val autoCompleteData = rawAutoCompleteData
    .distinctUntilChanged()
    .debounce(300, TimeUnit.MILLISECONDS)
    .switchMap { (sessionOptional, id, lastWord) ->
      val session = sessionOptional.orNull()
      val bufferSyncer = session?.bufferSyncer
      val bufferInfo = bufferSyncer?.bufferInfo(id)
      if (bufferSyncer != null) {
        session.liveNetworks().switchMap { networks ->
          bufferSyncer.liveBufferInfos().switchMap { infos ->
            if (bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
              val network = networks[bufferInfo.networkId]
              val ircChannel = network?.ircChannel(
                bufferInfo.bufferName
              )
              if (ircChannel != null) {
                ircChannel.liveIrcUsers().switchMap { users ->
                  val buffers: List<Observable<AutoCompleteItem.ChannelItem>?> = infos.values
                    .filter {
                      it.type.toInt() == Buffer_Type.ChannelBuffer.toInt()
                    }.mapNotNull { info ->
                      networks[info.networkId]?.let { info to it }
                    }.map { (info, network) ->
                      network.liveIrcChannel(
                        info.bufferName
                      ).switchMap { channel ->
                        channel.liveUpdates().map {
                          AutoCompleteItem.ChannelItem(
                            info = info,
                            network = network.networkInfo(),
                            bufferStatus = when (it) {
                              IrcChannel.NULL -> BufferStatus.OFFLINE
                              else            -> BufferStatus.ONLINE
                            },
                            description = it.topic()
                          )
                        }
                      }
                    }
                  val nicks = users.map<IrcUser, Observable<AutoCompleteItem.UserItem>?> {
                    it.updates().map { user ->
                      val userModes = ircChannel.userModes(user)
                      val prefixModes = network.prefixModes()

                      val lowestMode = userModes.mapNotNull(prefixModes::indexOf).min()
                                       ?: prefixModes.size

                      AutoCompleteItem.UserItem(
                        user.nick(),
                        network.modesToPrefixes(userModes),
                        lowestMode,
                        user.realName(),
                        user.isAway(),
                        network.support("CASEMAPPING"),
                        Regex("[us]id(\\d+)").matchEntire(user.user())?.groupValues?.lastOrNull()?.let {
                          "https://www.irccloud.com/avatar-redirect/$it"
                        }
                      )
                    }
                  }

                  combineLatest<AutoCompleteItem>(nicks + buffers)
                    .map { list ->
                      val ignoredStartingCharacters = charArrayOf(
                        '-', '_', '[', ']', '{', '}', '|', '`', '^', '.', '\\', '@'
                      )

                      Pair(
                        lastWord.first,
                        list.filter {
                          it.name.trimStart(*ignoredStartingCharacters)
                            .startsWith(
                              lastWord.first.trimStart(*ignoredStartingCharacters),
                              ignoreCase = true
                            )
                        }.sorted()
                      )
                    }
                }
              } else {
                Observable.just(Pair(lastWord.first, emptyList()))
              }
            } else {
              Observable.just(Pair(lastWord.first, emptyList()))
            }
          }
        }
      } else {
        Observable.just(Pair(lastWord.first, emptyList()))
      }
    }

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
  val collapsedNetworks = BehaviorSubject.createDefault(emptySet<NetworkId>())
  val selectedBufferId = BehaviorSubject.createDefault(-1)
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

          val info = bufferSyncer.bufferInfo(buffer)
          if (info != null) {
            val network = networks[info.networkId]
            when (info.type.enabledValues().firstOrNull()) {
              Buffer_Type.StatusBuffer  -> {
                network?.live_connectionState?.map {
                  SelectedBufferItem(
                    info,
                    connectionState = it,
                    hiddenState = hiddenState
                  )
                } ?: Observable.just(SelectedBufferItem(info, hiddenState = hiddenState))
              }
              Buffer_Type.ChannelBuffer -> {
                network?.liveIrcChannel(info.bufferName)?.map {
                  SelectedBufferItem(
                    info,
                    joined = it != IrcChannel.NULL,
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
  val selectedBuffer_liveData = selectedBuffer.toLiveData()

  val bufferList: Observable<Pair<BufferViewConfig?, List<BufferProps>>?> =
    combineLatest(session, bufferViewConfig, showHidden)
      .switchMap { (sessionOptional, configOptional, showHiddenRaw) ->
        val session = sessionOptional.orNull()
        val bufferSyncer = session?.bufferSyncer
        val showHidden = showHiddenRaw ?: false
        val config = configOptional.orNull()
        if (bufferSyncer != null && config != null) {
          session.liveNetworks().switchMap { networks ->
            config.liveUpdates()
              .debounce(16, TimeUnit.MILLISECONDS)
              .switchMap { currentConfig ->
                combineLatest<Collection<BufferId>>(
                  listOf(
                    config.liveBuffers(),
                    config.liveTemporarilyRemovedBuffers(),
                    config.liveRemovedBuffers()
                  )
                ).switchMap { (ids, temp, perm) ->
                  fun transformIds(ids: Collection<BufferId>, state: BufferHiddenState) =
                    ids.mapNotNull { id ->
                      bufferSyncer.bufferInfo(id)
                    }.filter {
                      currentConfig.networkId() <= 0 || currentConfig.networkId() == it.networkId
                    }.filter {
                      (currentConfig.allowedBufferTypes() and it.type).isNotEmpty() ||
                      it.type.hasFlag(Buffer_Type.StatusBuffer)
                    }.mapNotNull {
                      val network = networks[it.networkId]
                      if (network == null) {
                        null
                      } else {
                        it to network
                      }
                    }.filter {
                      !config.hideInactiveNetworks() || it.second.isConnected()
                    }.map<Pair<BufferInfo, Network>, Observable<BufferProps>?> { (info, network) ->
                      bufferSyncer.liveActivity(info.bufferId).switchMap { activity ->
                        bufferSyncer.liveHighlightCount(info.bufferId).map { highlights ->
                          activity to highlights
                        }
                      }.switchMap { (activity, highlights) ->
                        when (info.type.toInt()) {
                          BufferInfo.Type.QueryBuffer.toInt()   -> {
                            network.liveNetworkInfo().switchMap { networkInfo ->
                              network.liveIrcUser(info.bufferName).switchMap {
                                it.updates().map { user ->
                                  BufferProps(
                                    info = info,
                                    network = networkInfo,
                                    bufferStatus = when {
                                      user == IrcUser.NULL -> BufferStatus.OFFLINE
                                      user.isAway()        -> BufferStatus.AWAY
                                      else                 -> BufferStatus.ONLINE
                                    },
                                    description = user.realName(),
                                    activity = activity,
                                    highlights = highlights,
                                    hiddenState = state
                                  )
                                }
                              }
                            }
                          }
                          BufferInfo.Type.ChannelBuffer.toInt() -> {
                            network.liveNetworkInfo().switchMap { networkInfo ->
                              network.liveIrcChannel(info.bufferName).switchMap { channel ->
                                channel.liveUpdates().map {
                                  BufferProps(
                                    info = info,
                                    network = networkInfo,
                                    bufferStatus = when (it) {
                                      IrcChannel.NULL -> BufferStatus.OFFLINE
                                      else            -> BufferStatus.ONLINE
                                    },
                                    description = it.topic(),
                                    activity = activity,
                                    highlights = highlights,
                                    hiddenState = state
                                  )
                                }
                              }
                            }
                          }
                          BufferInfo.Type.StatusBuffer.toInt()  -> {
                            network.liveNetworkInfo().switchMap { networkInfo ->
                              network.live_connectionState.map {
                                BufferProps(
                                  info = info,
                                  network = networkInfo,
                                  bufferStatus = BufferStatus.OFFLINE,
                                  description = "",
                                  activity = activity,
                                  highlights = highlights,
                                  hiddenState = state
                                )
                              }
                            }
                          }
                          else                                  -> Observable.just(
                            BufferProps(
                              info = info,
                              network = network.networkInfo(),
                              bufferStatus = BufferStatus.OFFLINE,
                              description = "",
                              activity = activity,
                              highlights = highlights,
                              hiddenState = state
                            )
                          )
                        }
                      }
                    }

                  bufferSyncer.liveBufferInfos().switchMap {
                    val buffers = if (showHidden) {
                      transformIds(ids, BufferHiddenState.VISIBLE) +
                      transformIds(temp - ids, BufferHiddenState.HIDDEN_TEMPORARY) +
                      transformIds(perm - temp - ids, BufferHiddenState.HIDDEN_PERMANENT)
                    } else {
                      transformIds(ids.distinct(), BufferHiddenState.VISIBLE)
                    }

                    combineLatest<BufferProps>(buffers).map { list ->
                      Pair<BufferViewConfig?, List<BufferProps>>(
                        config,
                        list.filter {
                          (!config.hideInactiveBuffers()) ||
                          it.bufferStatus != BufferStatus.OFFLINE ||
                          it.info.type.hasFlag(Buffer_Type.StatusBuffer)
                        }.let {
                          if (config.sortAlphabetically())
                            it.sortedBy { IrcCaseMappers.unicode.toLowerCaseNullable(it.info.bufferName) }
                              .sortedByDescending { it.hiddenState == BufferHiddenState.VISIBLE }
                          else it
                        }.distinctBy { it.info.bufferId }
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
