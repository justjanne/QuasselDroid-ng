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

package de.kuschku.quasseldroid.service

import de.kuschku.libquassel.connection.SocketAddress
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.util.compatibility.HandlerService

class AsyncBackend(
  private val handler: HandlerService,
  private val backend: Backend,
  private val disconnectCallback: () -> Unit
) : Backend {
  override fun updateUserDataAndLogin(user: String, pass: String) {
    handler.backend {
      backend.updateUserDataAndLogin(user, pass)
    }
  }

  override fun connectUnlessConnected(address: SocketAddress, user: String, pass: String,
                                      reconnect: Boolean) {
    handler.backend {
      backend.connectUnlessConnected(address, user, pass, reconnect)
    }
  }

  override fun connect(address: SocketAddress, user: String, pass: String, reconnect: Boolean) {
    handler.backend {
      backend.connect(address, user, pass, reconnect)
    }
  }

  override fun reconnect() {
    handler.backend {
      backend.reconnect()
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
}
