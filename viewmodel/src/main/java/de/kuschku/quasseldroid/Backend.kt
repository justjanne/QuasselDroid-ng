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

package de.kuschku.quasseldroid

import de.kuschku.libquassel.connection.SocketAddress
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.session.SessionManager

interface Backend {
  fun autoConnect(
    ignoreConnectionState: Boolean = false,
    ignoreSetting: Boolean = false,
    ignoreErrors: Boolean = false,
    connectionInfo: ConnectionInfo? = null
  )

  fun disconnect(forever: Boolean = false)
  fun sessionManager(): SessionManager?
  fun updateUserDataAndLogin(user: String, pass: String)
  fun requestConnectNewNetwork()

  fun setCurrentBuffer(id: BufferId)

  data class ConnectionInfo(
    val address: SocketAddress,
    val username: String,
    val password: String,
    val requireSsl: Boolean,
    val shouldReconnect: Boolean
  )
}
