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

package de.justjanne.libquassel.protocol.serializers

import de.justjanne.libquassel.protocol.serializers.primitive.*
import de.justjanne.libquassel.protocol.variant.QtType
import java.util.*

object QtSerializers {
  private val serializers = setOf<QtSerializer<*>>(
    VoidSerializer,
    BoolSerializer,

    ByteSerializer,
    UByteSerializer,
    ShortSerializer,
    UShortSerializer,
    IntSerializer,
    UIntSerializer,
    LongSerializer,
    ULongSerializer,

    FloatSerializer,
    DoubleSerializer,

    QCharSerializer,
    StringSerializerUtf16,
    QStringListSerializer,
    ByteBufferSerializer,

    DateSerializer,
    TimeSerializer,
    DateTimeSerializer,

    QVariantSerializer,
    QVariantListSerializer,
    QVariantMapSerializer,
  ).associateBy(QtSerializer<*>::qtType)

  operator fun get(type: QtType) = serializers[type]

  @Suppress("UNCHECKED_CAST")
  inline fun <reified T> find(type: QtType): QtSerializer<T> {
    val serializer = get(type)
      ?: throw NoSerializerForTypeException.Qt(type, T::class.java)
    if (serializer.javaType == T::class.java) {
      return serializer as QtSerializer<T>
    } else {
      throw NoSerializerForTypeException.Qt(type, T::class.java)
    }
  }
}
