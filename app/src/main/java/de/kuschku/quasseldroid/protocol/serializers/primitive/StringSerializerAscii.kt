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
import de.kuschku.quasseldroid.protocol.io.stringEncoderAscii
import de.kuschku.quasseldroid.protocol.serializers.QtSerializer
import de.kuschku.quasseldroid.protocol.variant.QtType
import java.nio.ByteBuffer

object StringSerializerAscii : QtSerializer<String?> {
  override val qtType = QtType.QString
  override val javaType: Class<out String> = String::class.java

  override fun serialize(buffer: ChainedByteBuffer, data: String?) {
    if (data == null) {
        IntSerializer.serialize(buffer, -1)
    } else {
      val stringBuffer = stringEncoderAscii().encode(data, true)
        IntSerializer.serialize(buffer, stringBuffer.remaining())
      buffer.put(stringBuffer)
    }
  }

  override fun deserialize(buffer: ByteBuffer): String? {
    val length = IntSerializer.deserialize(buffer) - 1
    return if (length < 0) {
      null
    } else {
      stringEncoderAscii().decode(buffer, length, true)
    }
  }
}
