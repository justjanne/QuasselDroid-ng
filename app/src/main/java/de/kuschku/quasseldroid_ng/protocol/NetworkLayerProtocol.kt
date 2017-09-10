package de.kuschku.quasseldroid_ng.protocol

enum class NetworkLayerProtocol(val value: Byte) {
  IPv4Protocol(0),
  IPv6Protocol(1),
  AnyIPProtocol(2),
  UnknownNetworkLayerProtocol(-1);

  companion object {
    private val byId = NetworkLayerProtocol.values().associateBy(NetworkLayerProtocol::value)
    fun of(value: Byte) = byId[value] ?: NetworkLayerProtocol.UnknownNetworkLayerProtocol
  }
}
