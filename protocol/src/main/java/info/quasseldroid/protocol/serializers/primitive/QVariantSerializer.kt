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

package info.quasseldroid.protocol.serializers.primitive

import info.quasseldroid.protocol.features.FeatureSet
import info.quasseldroid.protocol.io.ChainedByteBuffer
import info.quasseldroid.protocol.serializers.NoSerializerForTypeException
import info.quasseldroid.protocol.serializers.QtSerializers
import info.quasseldroid.protocol.serializers.QuasselSerializers
import info.quasseldroid.protocol.variant.QVariant
import info.quasseldroid.protocol.variant.QVariant_
import info.quasseldroid.protocol.variant.QtType
import info.quasseldroid.protocol.variant.QuasselType
import java.nio.ByteBuffer

object QVariantSerializer : QtSerializer<QVariant_> {
  override val qtType = QtType.QVariant
  override val javaType: Class<QVariant_> = QVariant::class.java

  override fun serialize(buffer: ChainedByteBuffer, data: QVariant_, featureSet: FeatureSet) {
    IntSerializer.serialize(buffer, data.serializer.qtType.id, featureSet)
    BoolSerializer.serialize(buffer, false, featureSet)
    if (data is QVariant.Custom) {
      StringSerializerAscii.serialize(buffer, data.serializer.quasselType.typeName, featureSet)
    }
    data.serialize(buffer, featureSet)
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet): QVariant_ {
    val rawType = IntSerializer.deserialize(buffer, featureSet)
    val qtType = QtType.of(rawType)
      ?: throw NoSerializerForTypeException.Qt(rawType, null)
    // isNull, but we ignore it as it has no meaning
    BoolSerializer.deserialize(buffer, featureSet)

    return if (qtType == QtType.UserType) {
      val name = StringSerializerAscii.deserialize(buffer, featureSet)
      val quasselType = QuasselType.of(name)
        ?: throw NoSerializerForTypeException.Quassel(qtType.id, name)
      deserialize(quasselType, buffer, featureSet)
    } else {
      deserialize(qtType, buffer, featureSet)
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun deserialize(type: QtType, buffer: ByteBuffer, featureSet: FeatureSet): QVariant_ {
    val serializer = QtSerializers[type]
      ?: throw NoSerializerForTypeException.Qt(type)
    val value = serializer.deserialize(buffer, featureSet)
    return QVariant.of(value, serializer as QtSerializer<Any?>)
  }

  @Suppress("UNCHECKED_CAST")
  private fun deserialize(type: QuasselType, buffer: ByteBuffer, featureSet: FeatureSet): QVariant_ {
    val serializer = QuasselSerializers[type]
      ?: throw NoSerializerForTypeException.Quassel(type)
    val value = serializer.deserialize(buffer, featureSet)
    return QVariant.of(value, serializer as QuasselSerializer<Any?>)
  }
}
