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

package de.kuschku.quasseldroid.viewmodel

import android.os.Bundle
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.util.helper.safeValue
import io.reactivex.subjects.BehaviorSubject

open class ArchiveViewModel : QuasselViewModel() {
  val bufferViewConfigId = BehaviorSubject.createDefault(-1)
  val visibleExpandedNetworks = BehaviorSubject.createDefault(emptyMap<NetworkId, Boolean>())
  val temporarilyExpandedNetworks = BehaviorSubject.createDefault(emptyMap<NetworkId, Boolean>())
  val permanentlyExpandedNetworks = BehaviorSubject.createDefault(emptyMap<NetworkId, Boolean>())
  val selectedBufferId = BehaviorSubject.createDefault(BufferId.MAX_VALUE)

  fun onSaveInstanceState(outState: Bundle) {
    outState.putInt(
      KEY_BUFFER_VIEW_CONFIG_ID,
      bufferViewConfigId.safeValue)
    outState.putSerializable(
      KEY_VISIBLE_EXPANDED_NETWORKS,
      HashMap(visibleExpandedNetworks.safeValue))
    outState.putSerializable(
      KEY_TEMPORARILY_EXPANDED_NETWORKS,
      HashMap(temporarilyExpandedNetworks.safeValue))
    outState.putSerializable(
      KEY_PERMANENTLY_EXPANDED_NETWORKS,
      HashMap(permanentlyExpandedNetworks.safeValue))
    outState.putInt(
      KEY_SELECTED_BUFFER_ID,
      selectedBufferId.safeValue.id)
  }

  fun onRestoreInstanceState(savedInstanceState: Bundle) {
    if (savedInstanceState.containsKey(KEY_BUFFER_VIEW_CONFIG_ID))
      bufferViewConfigId.onNext(savedInstanceState.getInt(KEY_BUFFER_VIEW_CONFIG_ID))
    if (savedInstanceState.containsKey(KEY_VISIBLE_EXPANDED_NETWORKS)) {
      visibleExpandedNetworks.onNext(
        savedInstanceState.getSerializable(KEY_VISIBLE_EXPANDED_NETWORKS) as? HashMap<NetworkId, Boolean>
        ?: emptyMap()
      )
    }
    if (savedInstanceState.containsKey(KEY_TEMPORARILY_EXPANDED_NETWORKS)) {
      temporarilyExpandedNetworks.onNext(
        savedInstanceState.getSerializable(KEY_TEMPORARILY_EXPANDED_NETWORKS) as? HashMap<NetworkId, Boolean>
        ?: emptyMap()
      )
    }
    if (savedInstanceState.containsKey(KEY_PERMANENTLY_EXPANDED_NETWORKS)) {
      permanentlyExpandedNetworks.onNext(
        savedInstanceState.getSerializable(KEY_PERMANENTLY_EXPANDED_NETWORKS) as? HashMap<NetworkId, Boolean>
        ?: emptyMap()
      )
    }

    if (savedInstanceState.containsKey(KEY_SELECTED_BUFFER_ID))
      selectedBufferId.onNext(BufferId(savedInstanceState.getInt(KEY_SELECTED_BUFFER_ID)))
  }

  companion object {
    const val KEY_BUFFER_VIEW_CONFIG_ID = "model_archive_bufferViewConfigId"
    const val KEY_VISIBLE_EXPANDED_NETWORKS = "model_archive_visibleExpandedNetworks"
    const val KEY_TEMPORARILY_EXPANDED_NETWORKS = "model_archive_temporarilyExpandedNetworks"
    const val KEY_PERMANENTLY_EXPANDED_NETWORKS = "model_archive_permanentlyExpandedNetworks"
    const val KEY_SELECTED_BUFFER_ID = "model_archive_selectedBufferId"
  }
}
