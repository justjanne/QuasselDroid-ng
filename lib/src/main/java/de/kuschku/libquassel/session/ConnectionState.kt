package de.kuschku.libquassel.session

enum class ConnectionState {
  DISCONNECTED,
  CONNECTING,
  HANDSHAKE,
  INIT,
  CONNECTED,
  CLOSED
}
