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
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helper.*
import de.kuschku.libquassel.util.irc.IrcCaseMappers
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
  val markerLine = connectedSession.mapSwitchMap { currentSession ->
    chat.bufferId.safeSwitchMap { currentBuffer ->
      // Get a stream of the latest marker line
      currentSession.bufferSyncer.liveMarkerLine(currentBuffer)
    }
  }

  val bufferData = combineLatest(connectedSession, chat.bufferId)
    .safeSwitchMap { (sessionOptional, id) ->
      val session = sessionOptional.orNull()
      val bufferSyncer = session?.bufferSyncer
      if (bufferSyncer != null) {
        session.liveNetworks().safeSwitchMap { networks ->
          bufferSyncer.liveBufferInfos().safeSwitchMap {
            val info = bufferSyncer.bufferInfo(id)
            val network = networks[info?.networkId]
            if (info == null || network == null) {
              Observable.just(BufferData())
            } else {
              when (info.type.toInt()) {
                BufferInfo.Type.QueryBuffer.toInt()   -> {
                  network.liveIrcUser(info.bufferName).safeSwitchMap {
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
                  ).safeSwitchMap { channel ->
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

  val nickData: Observable<List<IrcUserItem>> = combineLatest(connectedSession, chat.bufferId)
    .safeSwitchMap { (sessionOptional, buffer) ->
      val session = sessionOptional.orNull()
      val bufferSyncer = session?.bufferSyncer
      val bufferInfo = bufferSyncer?.bufferInfo(buffer)
      if (bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
        session.liveNetworks().safeSwitchMap { networks ->
          val network = networks[bufferInfo.networkId]
          network?.liveIrcChannel(bufferInfo.bufferName)?.switchMapNullable(IrcChannel.NULL) { ircChannel ->
            ircChannel?.liveIrcUsers()?.safeSwitchMap { users ->
              combineLatest<IrcUserItem>(
                users.map<IrcUser, Observable<IrcUserItem>?> {
                  it.updates().map { user ->
                    val userModes = ircChannel.userModes(user)
                    val prefixModes = network.prefixModes()

                    val lowestMode = userModes.asSequence().mapNotNull {
                      prefixModes.indexOf(it)
                    }.min() ?: prefixModes.size

                    IrcUserItem(
                      bufferInfo.networkId,
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
          } ?: Observable.just(emptyList())
        }
      } else {
        Observable.just(emptyList())
      }
    }
  val nickDataThrottled =
    nickData.distinctUntilChanged().throttleLast(100, TimeUnit.MILLISECONDS)

  val selectedBuffer = processSelectedBuffer(chat.selectedBufferId, bufferViewConfig)

  val bufferList: Observable<Pair<BufferViewConfig?, List<BufferProps>>> =
    combineLatest(connectedSession, bufferViewConfig, chat.showHidden, chat.bufferSearch)
      .safeSwitchMap { (sessionOptional, configOptional, showHiddenRaw, bufferSearch) ->
        val session = sessionOptional.orNull()
        val bufferSyncer = session?.bufferSyncer
        val showHidden = showHiddenRaw ?: false
        val config = configOptional.orNull()
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
                    processRawBufferList(
                      ids,
                      state,
                      bufferSyncer,
                      networks,
                      currentConfig,
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

  val processedBufferList = processInternalBufferList(
    bufferList,
    chat.expandedNetworks,
    chat.selectedBufferId,
    false
  )
}
