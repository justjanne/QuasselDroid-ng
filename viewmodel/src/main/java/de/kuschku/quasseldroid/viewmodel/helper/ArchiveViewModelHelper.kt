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
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.flatMapSwitchMap
import de.kuschku.libquassel.util.helper.mapSwitchMap
import de.kuschku.libquassel.util.helper.safeSwitchMap
import de.kuschku.libquassel.util.irc.IrcCaseMappers
import de.kuschku.quasseldroid.viewmodel.ArchiveViewModel
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid.viewmodel.data.BufferHiddenState
import de.kuschku.quasseldroid.viewmodel.data.BufferProps
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import io.reactivex.Observable
import javax.inject.Inject

open class ArchiveViewModelHelper @Inject constructor(
  val archive: ArchiveViewModel,
  quassel: QuasselViewModel
) : QuasselViewModelHelper(quassel) {
  val bufferViewConfig = bufferViewManager.flatMapSwitchMap { manager ->
    archive.bufferViewConfigId.map { id ->
      Optional.ofNullable(manager.bufferViewConfig(id))
    }.mapSwitchMap(BufferViewConfig::liveUpdates)
  }

  fun processBufferList(bufferListType: BufferHiddenState) =
    combineLatest(connectedSession, bufferViewConfig)
      .safeSwitchMap { (sessionOptional, configOptional) ->
        val session = sessionOptional.orNull()
        val bufferSyncer = session?.bufferSyncer
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
                  fun missingStatusBuffers(
                    list: Collection<BufferId>
                  ): Sequence<Observable<BufferProps>?> {
                    val buffers = list.asSequence().mapNotNull { id ->
                      bufferSyncer.bufferInfo(id)
                    }

                    val totalNetworks = buffers.filter {
                      !it.type.hasFlag(Buffer_Type.StatusBuffer)
                    }.map {
                      it.networkId
                    }.toList()

                    val availableNetworks = buffers.filter {
                      it.type.hasFlag(Buffer_Type.StatusBuffer)
                    }.map {
                      it.networkId
                    }.toList()

                    val wantedNetworks = if (!currentConfig.networkId().isValidId()) totalNetworks
                    else listOf(currentConfig.networkId())

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

                  fun transformIds(ids: Collection<BufferId>, state: BufferHiddenState) =
                    processRawBufferList(ids, state, bufferSyncer, networks, currentConfig) +
                    missingStatusBuffers(ids)

                  bufferSyncer.liveBufferInfos().safeSwitchMap {
                    val buffers = when (bufferListType) {
                      BufferHiddenState.VISIBLE          ->
                        transformIds(ids, BufferHiddenState.VISIBLE)
                      BufferHiddenState.HIDDEN_TEMPORARY ->
                        transformIds(temp - ids, BufferHiddenState.HIDDEN_TEMPORARY)
                      BufferHiddenState.HIDDEN_PERMANENT ->
                        transformIds(perm - temp - ids, BufferHiddenState.HIDDEN_PERMANENT)
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

  fun processArchiveBufferList(
    bufferListType: BufferHiddenState,
    showHandle: Boolean
  ) = processInternalBufferList(
    processBufferList(bufferListType),
    when (bufferListType) {
      BufferHiddenState.VISIBLE          -> archive.visibleExpandedNetworks
      BufferHiddenState.HIDDEN_TEMPORARY -> archive.temporarilyExpandedNetworks
      BufferHiddenState.HIDDEN_PERMANENT -> archive.permanentlyExpandedNetworks
    },
    archive.selectedBufferId,
    showHandle
  )

  val selectedBuffer = processSelectedBuffer(archive.selectedBufferId, bufferViewConfig)
}
