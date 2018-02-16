package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.NetworkLayerProtocol
import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.nio.ByteBuffer

object HostAddressSerializer : Serializer<InetAddress> {
  override fun serialize(buffer: ChainedByteBuffer, data: InetAddress, features: Quassel_Features) {
    when (data) {
      is Inet4Address -> {
        ByteSerializer.serialize(buffer, NetworkLayerProtocol.IPv4Protocol.value, features)
        buffer.put(data.address)
      }
      is Inet6Address -> {
        ByteSerializer.serialize(buffer, NetworkLayerProtocol.IPv6Protocol.value, features)
        buffer.put(data.address)
      }
      else            -> {
        ByteSerializer.serialize(
          buffer, NetworkLayerProtocol.UnknownNetworkLayerProtocol.value,
          features
        )
        throw IllegalArgumentException("Invalid network protocol ${data.javaClass.canonicalName}")
      }
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): InetAddress {
    val type = ByteSerializer.deserialize(buffer, features)
    return when (NetworkLayerProtocol.of(type)) {
      NetworkLayerProtocol.IPv4Protocol -> {
        val buf = ByteArray(4)
        buffer.get(buf)
        Inet4Address.getByAddress(buf)
      }
      NetworkLayerProtocol.IPv6Protocol -> {
        val buf = ByteArray(16)
        buffer.get(buf)
        Inet6Address.getByAddress(buf)
      }
      else                              -> {
        throw IllegalArgumentException("Invalid network protocol $type")
      }
    }
  }
}
