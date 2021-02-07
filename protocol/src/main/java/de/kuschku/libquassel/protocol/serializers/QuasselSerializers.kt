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

import de.kuschku.libquassel.protocol.serializers.primitive.*
import de.kuschku.libquassel.protocol.variant.QuasselType
import java.util.*

object QuasselSerializers {
  private val serializers = listOf<QuasselSerializer<*>>(
    BufferIdSerializer,
    BufferInfoSerializer,
    //DccConfigIpDetectionModeSerializer,
    //DccConfigPortSelectionModeSerializer,
    //IrcUserSerializer,
    //IrcChannelSerializer,
    //IdentitySerializer,
    IdentityIdSerializer,
    MessageSerializer,
    MsgIdSerializer,
    NetworkIdSerializer,
    //NetworkInfoSerializer,
    //NetworkServerSerializer,
    //QHostAddressSerializer,
    PeerPtrSerializer,
  ).associateBy(QuasselSerializer<*>::quasselType)

  operator fun get(type: QuasselType) = serializers[type]

  @Suppress("UNCHECKED_CAST")
  inline fun <reified T> find(type: QuasselType): QuasselSerializer<T> {
    val serializer = get(type)
      ?: throw NoSerializerForTypeException.Quassel(type, T::class.java)
    if (serializer.javaType == T::class.java) {
      return serializer as QuasselSerializer<T>
    } else {
      throw NoSerializerForTypeException.Quassel(type, T::class.java)
    }
  }
}
