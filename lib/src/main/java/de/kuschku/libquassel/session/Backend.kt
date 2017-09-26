package de.kuschku.libquassel.session

interface Backend {
  fun connect(address: SocketAddress, user: String, pass: String)
  fun disconnect()
  fun sessionManager(): SessionManager
}
