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

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helper.*
import de.kuschku.quasseldroid.util.safety.DeceptiveNetworkManager
import de.kuschku.quasseldroid.viewmodel.ChatViewModel
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid.viewmodel.data.BufferData
import de.kuschku.quasseldroid.viewmodel.data.IrcUserItem
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open class ChatViewModelHelper @Inject constructor(
  val chat: ChatViewModel,
  val deceptiveNetworkManager: DeceptiveNetworkManager,
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
                users.mapNotNull<IrcUser, Observable<IrcUserItem>> {
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
                }.toList()
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

  val selectedBuffer = processSelectedBuffer(bufferViewConfig, chat.selectedBufferId)

  val deceptiveNetwork =
    network.mapSwitchMap(Network::liveNetworkInfo)
      .mapMap(deceptiveNetworkManager::isDeceptive)
      .mapOrElse(false)

  fun processChatBufferList(
    filtered: Observable<Pair<Map<BufferId, Int>, Int>>
  ) = filterBufferList(
    processBufferList(
      bufferViewConfig,
      filtered,
      bufferSearch = chat.bufferSearch
    ),
    chat.expandedNetworks,
    chat.selectedBufferId,
    showHandle = false
  )
}
