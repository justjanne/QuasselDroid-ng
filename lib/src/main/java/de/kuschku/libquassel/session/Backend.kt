package de.kuschku.libquassel.session

import de.kuschku.libquassel.connection.SocketAddress

interface Backend {
  fun connectUnlessConnected(address: SocketAddress, user: String, pass: String, reconnect: Boolean)
  fun connect(address: SocketAddress, user: String, pass: String, reconnect: Boolean)
  fun reconnect()
  fun disconnect(forever: Boolean = false)
  fun sessionManager(): SessionManager
  fun updateUserDataAndLogin(user: String, pass: String)
}
