package de.kuschku.libquassel.session

import java.net.InetSocketAddress

data class SocketAddress(val host: String, val port: Int) {
  fun data() = InetSocketAddress(host, port)
}
