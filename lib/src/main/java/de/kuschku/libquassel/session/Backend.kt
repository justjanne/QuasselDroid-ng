package de.kuschku.libquassel.session

interface Backend {
  fun connectUnlessConnected(address: SocketAddress, user: String, pass: String, reconnect: Boolean)
  fun connect(address: SocketAddress, user: String, pass: String, reconnect: Boolean)
  fun reconnect()
  fun disconnect()
  fun sessionManager(): SessionManager
}
