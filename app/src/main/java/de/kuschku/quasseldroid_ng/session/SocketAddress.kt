package de.kuschku.quasseldroid_ng.session

import java.net.InetSocketAddress

data class SocketAddress(val host: String, val port: Short) {
  fun data() = InetSocketAddress(host, port.toInt())
}
