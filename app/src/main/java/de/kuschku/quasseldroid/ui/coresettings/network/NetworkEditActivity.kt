/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
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

package de.kuschku.quasseldroid.ui.coresettings.network

import android.content.Context
import android.content.Intent
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.quasseldroid.util.ui.ServiceBoundSettingsActivity

class NetworkEditActivity : ServiceBoundSettingsActivity(NetworkEditFragment()) {
  companion object {
    fun launch(
      context: Context,
      network: NetworkId
    ) = context.startActivity(intent(context, network))

    fun intent(
      context: Context,
      network: NetworkId
    ) = Intent(context, NetworkEditActivity::class.java).apply {
      putExtra("network", network)
    }
  }
}
