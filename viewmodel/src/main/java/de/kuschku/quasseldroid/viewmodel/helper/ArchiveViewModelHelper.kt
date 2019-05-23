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
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helper.flatMapSwitchMap
import de.kuschku.libquassel.util.helper.mapSwitchMap
import de.kuschku.quasseldroid.viewmodel.ArchiveViewModel
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid.viewmodel.data.BufferHiddenState
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

  fun processArchiveBufferList(
    bufferListType: BufferHiddenState,
    showHandle: Boolean,
    filtered: Observable<Pair<Map<BufferId, Int>, Int>>
  ) = filterBufferList(
    processRawBufferList(
      bufferViewConfig,
      filtered,
      bufferListType = bufferListType,
      showAllNetworks = false
    ),
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
