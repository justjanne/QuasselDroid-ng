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

package de.kuschku.quasseldroid.ui.coresettings.network

import de.kuschku.libquassel.util.helper.value
import de.kuschku.quasseldroid.Backend

class NetworkCreateFragment : NetworkBaseFragment(true) {
  override fun onSave() = modelHelper.connectedSession.value?.orNull()?.let { session ->
    network?.let { (_, data) ->
      applyChanges(data)
      modelHelper.backend.value?.ifPresent(Backend::requestConnectNewNetwork)
      session.rpcHandler.createNetwork(data.networkInfo(), emptyList())
      true
    }
  } ?: false
}
