package de.kuschku.libquassel.session

interface Backend {
  fun connectUnlessConnected(address: SocketAddress, user: String, pass: String)
  fun connect(address: SocketAddress, user: String, pass: String)
  fun disconnect()
  fun sessionManager(): SessionManager
}
