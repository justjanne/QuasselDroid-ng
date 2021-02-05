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

package de.kuschku.quasseldroid.protocol.serializers.primitive

import de.kuschku.quasseldroid.protocol.io.ChainedByteBuffer
import de.kuschku.quasseldroid.protocol.serializers.*
import de.kuschku.quasseldroid.protocol.variant.QVariant
import de.kuschku.quasseldroid.protocol.variant.QVariant_
import de.kuschku.quasseldroid.protocol.variant.QtType
import de.kuschku.quasseldroid.protocol.variant.QuasselType
import java.nio.ByteBuffer

object VariantSerializer : QtSerializer<QVariant_> {
  override val qtType = QtType.QVariant
  override val javaType: Class<QVariant_> = QVariant::class.java

  override fun serialize(buffer: ChainedByteBuffer, data: QVariant_) {
    IntSerializer.serialize(buffer, data.serializer.qtType.id)
    BoolSerializer.serialize(buffer, false)
    if (data is QVariant.Custom && data.serializer.qtType == QtType.UserType) {
      StringSerializerAscii.serialize(buffer, data.serializer.quasselType.typeName)
    }
    data.serialize(buffer)
  }

  override fun deserialize(buffer: ByteBuffer): QVariant_ {
    val rawType = IntSerializer.deserialize(buffer)
    val qtType = QtType.of(rawType)
      ?: throw NoSerializerForTypeException(rawType, null)
    // isNull, but we ignore it as it has no meaning
    BoolSerializer.deserialize(buffer)

    return if (qtType == QtType.UserType) {
      val name = StringSerializerAscii.deserialize(buffer)
      val quasselType = QuasselType.of(name)
        ?: throw NoSerializerForTypeException(qtType.id, name)
      deserialize(quasselType, buffer)
    } else {
      deserialize(qtType, buffer)
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun deserialize(type: QtType, buffer: ByteBuffer): QVariant_ {
    val serializer = Serializers[type]
      ?: throw NoSerializerForTypeException(type)
    val value = serializer.deserialize(buffer)
    return QVariant.of(value, serializer as QuasselSerializer<Any?>)
  }

  @Suppress("UNCHECKED_CAST")
  private fun deserialize(type: QuasselType, buffer: ByteBuffer): QVariant_ {
    val serializer = Serializers[type]
      ?: throw NoSerializerForTypeException(type)
    val value = serializer.deserialize(buffer)
    return QVariant.of(value, serializer as QuasselSerializer<Any?>)
  }
}
