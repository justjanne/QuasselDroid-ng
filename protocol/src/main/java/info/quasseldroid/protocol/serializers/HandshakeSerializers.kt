/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package info.quasseldroid.protocol.serializers

import info.quasseldroid.protocol.serializers.handshake.*
import info.quasseldroid.protocol.serializers.primitive.*
import java.util.*

object HandshakeSerializers {
  private val serializers = listOf<HandshakeSerializer<*>>(
    ClientInitSerializer,
    ClientInitAckSerializer,
    ClientInitRejectSerializer,

    CoreSetupDataSerializer,
    CoreSetupAckSerializer,
    CoreSetupRejectSerializer,

    ClientLoginSerializer,
    ClientLoginAckSerializer,
    ClientLoginRejectSerializer,

    SessionInitSerializer,
  ).associateBy(HandshakeSerializer<*>::type)

  operator fun get(type: String) = serializers[type]

  @Suppress("UNCHECKED_CAST")
  inline fun <reified T> find(type: String): HandshakeSerializer<T> {
    val serializer = get(type)
      ?: throw NoSerializerForTypeException.Handshake(type, T::class.java)
    if (serializer.javaType == T::class.java) {
      return serializer as HandshakeSerializer<T>
    } else {
      throw NoSerializerForTypeException.Handshake(type, T::class.java)
    }
  }
}
