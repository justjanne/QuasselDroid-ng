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

package de.kuschku.libquassel.protocol.serializers

import de.kuschku.libquassel.protocol.serializers.handshake.ClientInitAckSerializer
import de.kuschku.libquassel.protocol.serializers.handshake.ClientInitRejectSerializer
import de.kuschku.libquassel.protocol.serializers.handshake.ClientInitSerializer
import de.kuschku.libquassel.protocol.serializers.handshake.HandshakeSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.*
import java.util.*

object HandshakeSerializers {
  private val serializers = listOf<HandshakeSerializer<*>>(
    ClientInitSerializer,
    ClientInitAckSerializer,
    ClientInitRejectSerializer,
  ).associateBy(HandshakeSerializer<*>::type)

  @PublishedApi
  internal fun find(type: String) = serializers[type]

  @Suppress("UNCHECKED_CAST")
  inline operator fun <reified T> get(type: String): HandshakeSerializer<T> {
    val serializer = find(type)
      ?: throw NoSerializerForTypeException.Handshake(type, T::class.java)
    if (serializer.javaType == T::class.java) {
      return serializer as HandshakeSerializer<T>
    } else {
      throw NoSerializerForTypeException.Handshake(type, T::class.java)
    }
  }
}
