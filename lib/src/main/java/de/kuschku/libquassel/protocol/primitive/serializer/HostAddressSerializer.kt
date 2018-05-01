/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.NetworkLayerProtocol
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.nio.ByteBuffer

object HostAddressSerializer : Serializer<InetAddress> {
  override fun serialize(buffer: ChainedByteBuffer, data: InetAddress, features: QuasselFeatures) {
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

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): InetAddress {
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
