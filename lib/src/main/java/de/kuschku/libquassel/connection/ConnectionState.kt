package de.kuschku.libquassel.connection

enum class ConnectionState {
  DISCONNECTED,
  CONNECTING,
  HANDSHAKE,
  INIT,
  CONNECTED,
  CLOSED
}
