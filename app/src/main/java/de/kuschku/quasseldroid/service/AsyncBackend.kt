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
