/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package de.kuschku.quasseldroid.util.safety

import android.content.Context
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.util.ExpressionMatch
import de.kuschku.quasseldroid.viewmodel.R
import javax.inject.Inject

class DeceptiveNetworkManager @Inject constructor(context: Context) {
  private val untrustworthyNetworks: List<String> =
    context.resources.getStringArray(R.array.deceptive_networks).toList()

  private val matcher = ExpressionMatch(
    untrustworthyNetworks.joinToString("\n"),
    ExpressionMatch.MatchMode.MatchMultiWildcard,
    false
  )

  fun isDeceptive(info: INetwork.NetworkInfo): Boolean {
    return info.serverList.any {
      matcher.match(it.host ?: return false, false)
    }
  }
}
