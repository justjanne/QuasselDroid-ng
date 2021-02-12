/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package info.quasseldroid.protocol.serializers.primitive

import info.quasseldroid.protocol.features.FeatureSet
import info.quasseldroid.protocol.io.ChainedByteBuffer
import info.quasseldroid.protocol.types.NetworkLayerProtocol
import info.quasseldroid.protocol.variant.QuasselType
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.nio.ByteBuffer

object QHostAddressSerializer : QuasselSerializer<InetAddress> {
  override val quasselType: QuasselType = QuasselType.QHostAddress
  override val javaType: Class<out InetAddress> = InetAddress::class.java

  override fun serialize(
    buffer: ChainedByteBuffer,
    data: InetAddress,
    featureSet: FeatureSet
  ) {
    when (data) {
      is Inet4Address -> {
        UByteSerializer.serialize(
          buffer,
          NetworkLayerProtocol.IPv4Protocol.value,
          featureSet
        )
        buffer.put(data.address)
      }
      is Inet6Address -> {
        UByteSerializer.serialize(
          buffer,
          NetworkLayerProtocol.IPv6Protocol.value,
          featureSet
        )
        buffer.put(data.address)
      }
      else            -> {
        UByteSerializer.serialize(
          buffer,
          NetworkLayerProtocol.UnknownNetworkLayerProtocol.value,
          featureSet
        )
        throw IllegalArgumentException("Invalid network protocol ${data.javaClass.canonicalName}")
      }
    }
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet): InetAddress {
    val type = UByteSerializer.deserialize(buffer, featureSet)
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
