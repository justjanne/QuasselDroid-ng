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

package de.justjanne.libquassel.protocol.serializers.primitive

import de.justjanne.libquassel.protocol.features.FeatureSet
import de.justjanne.libquassel.protocol.io.ChainedByteBuffer
import de.justjanne.libquassel.protocol.io.StringEncoder
import de.justjanne.libquassel.protocol.variant.QtType
import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.concurrent.getOrSet

abstract class StringSerializer(
  private val charset: Charset,
  private val nullLimited: Boolean,
) : QtSerializer<String?> {
  override val qtType = QtType.QString
  override val javaType: Class<out String> = String::class.java

  private val encoderLocal = ThreadLocal<StringEncoder>()
  private fun encoder() = encoderLocal.getOrSet { StringEncoder(charset) }

  private fun addNullBytes(before: Int) = if (nullLimited) before + 1 else before
  private fun removeNullBytes(before: Int) = if (nullLimited) before - 1 else before

  override fun serialize(buffer: ChainedByteBuffer, data: String?, featureSet: FeatureSet) {
    if (data == null) {
      IntSerializer.serialize(buffer, -1, featureSet)
    } else {
      val encodedData = encoder().encode(data)
      IntSerializer.serialize(buffer, addNullBytes(encodedData.remaining()), featureSet)
      buffer.put(encodedData)
      if (nullLimited) {
        buffer.put(0)
      }
    }
  }

  override fun deserialize(buffer: ByteBuffer, featureSet: FeatureSet): String? {
    val length = IntSerializer.deserialize(buffer, featureSet)
    if (length < 0) {
      return null
    }
    val result = encoder().decode(buffer, removeNullBytes(length))
    if (nullLimited) {
      buffer.position(addNullBytes(buffer.position()))
    }
    return result
  }

  fun serializeRaw(data: String?): ByteBuffer {
    val result = encoder().encode(data)
    if (nullLimited) {
      val buffer = ByteBuffer.allocateDirect(result.remaining() + 1)
      buffer.put(result)
      buffer.clear()
      return buffer
    }
    return result
  }

  fun deserializeRaw(data: ByteBuffer): String {
    if (nullLimited) {
      data.limit(removeNullBytes(data.limit()))
    }
    return encoder().decode(data)
  }
}
