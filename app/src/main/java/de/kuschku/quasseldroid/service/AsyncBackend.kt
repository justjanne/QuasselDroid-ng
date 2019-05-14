/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.quasseldroid.service

import de.kuschku.libquassel.util.compatibility.HandlerService
import de.kuschku.quasseldroid.Backend

class AsyncBackend(
  private val handler: HandlerService,
  private val backend: Backend
) : Backend {
  private var disconnectCallback: (() -> Unit)? = null

  fun setDisconnectCallback(callback: (() -> Unit)?) {
    this.disconnectCallback = callback
  }

  override fun updateUserDataAndLogin(user: String, pass: String) {
    handler.backend {
      backend.updateUserDataAndLogin(user, pass)
    }
  }

  override fun autoConnect(
    ignoreConnectionState: Boolean,
    ignoreSetting: Boolean,
    ignoreErrors: Boolean,
    connectionInfo: Backend.ConnectionInfo?
  ) {
    handler.backend {
      backend.autoConnect(
        ignoreConnectionState,
        ignoreSetting,
        ignoreErrors,
        connectionInfo
      )
    }
  }

  override fun disconnect(forever: Boolean) {
    handler.backend {
      backend.disconnect(forever)
      if (forever) {
        disconnectCallback
      }
    }
  }

  override fun sessionManager() = backend.sessionManager()

  override fun requestConnectNewNetwork() {
    backend.requestConnectNewNetwork()
  }
}
