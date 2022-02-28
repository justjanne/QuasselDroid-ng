package de.justjanne.quasseldroid.service

import java.net.InetSocketAddress

data class ConnectionData(
  val address: InetSocketAddress,
  val username: String,
  val password: String
)
