/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
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

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer

object VariantSerializer : Serializer<QVariant_> {
  override fun serialize(buffer: ChainedByteBuffer, data: QVariant_, features: QuasselFeatures) {
    IntSerializer.serialize(buffer, data.type.id, features)
    BoolSerializer.serialize(buffer, false, features)
    if (data is QVariant.Custom && data.type == Type.UserType) {
      StringSerializer.C.serialize(buffer, data.qtype.typeName, features)
    }
    (data.serializer as Serializer<Any?>).serialize(buffer, data.data, features)
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): QVariant_ {
    val rawType = IntSerializer.deserialize(buffer, features)
    val type = Type.of(rawType)
    val isNull = BoolSerializer.deserialize(buffer, features)

    return if (type == Type.UserType) {
      val name = StringSerializer.C.deserialize(buffer, features)
      val qType = name?.let(QType.Companion::of)
                  ?: throw IllegalArgumentException("No such type: $name")
      val value = qType.serializer.deserialize(buffer, features)
      QVariant.of<All_>(value, qType)
    } else {
      val serializer = type?.serializer ?: throw IllegalArgumentException("No such type: $type")
      val value = serializer.deserialize(buffer, features)
      QVariant.of<All_>(value, type)
    }
  }
}
