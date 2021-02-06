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

package de.kuschku.libquassel.protocol.variant

import de.kuschku.libquassel.protocol.features.FeatureSet
import de.kuschku.libquassel.protocol.io.ChainedByteBuffer
import de.kuschku.libquassel.protocol.io.contentToString
import de.kuschku.libquassel.protocol.serializers.primitive.QtSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.QuasselSerializer
import de.kuschku.libquassel.protocol.serializers.primitive.serializerFor
import java.nio.ByteBuffer
import java.util.*

typealias QVariant_ = QVariant<*>
typealias QVariantList = List<QVariant_>
typealias QVariantMap = Map<String, QVariant_>
typealias QStringList = List<String?>

sealed class QVariant<T> constructor(
  internal val data: T,
  open val serializer: QtSerializer<T>,
) {
  class Typed<T> internal constructor(data: T, serializer: QtSerializer<T>) :
    QVariant<T>(data, serializer) {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Typed<*>) return false

      if (data != other.data) return false
      if (serializer.qtType != other.serializer.qtType) return false

      return true
    }

    override fun hashCode(): Int {
      var result = data?.hashCode() ?: 0
      result = 31 * result + serializer.qtType.hashCode()
      return result
    }
  }

  class Custom<T> internal constructor(data: T, override val serializer: QuasselSerializer<T>) :
    QVariant<T>(data, serializer) {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Custom<*>) return false

      if (data != other.data) return false
      if (serializer.quasselType != other.serializer.quasselType) return false

      return true
    }

    override fun hashCode(): Int {
      var result = data?.hashCode() ?: 0
      result = 31 * result + serializer.quasselType.hashCode()
      return result
    }
  }

  fun value(): T = data


  fun serialize(buffer: ChainedByteBuffer, featureSet: FeatureSet) {
    serializer.serialize(buffer, data, featureSet)
  }

  override fun toString() = when (data) {
    is ByteBuffer ->
      "QVariant(${serializer::class.java.simpleName}, ${data.contentToString()})"
    is Array<*> ->
      "QVariant(${serializer::class.java.simpleName}, ${Arrays.toString(data)})"
    else ->
      "QVariant(${serializer::class.java.simpleName}, $data)"
  }

  companion object {
    fun <T> of(data: T, serializer: QtSerializer<T>) = Typed(data, serializer)
    fun <T> of(data: T, serializer: QuasselSerializer<T>) = Custom(data, serializer)
  }
}

inline fun <reified T> qVariant(data: T, type: QtType): QVariant<T> =
  QVariant.of(data, serializerFor(type))

inline fun <reified T> qVariant(data: T, type: QuasselType): QVariant<T> =
  QVariant.of(data, serializerFor(type))

@Suppress("UNCHECKED_CAST")
inline fun <reified T> QVariant_.withType(): QVariant<T>? =
  if (this.serializer.javaType == T::class.java && this.value() is T) this as QVariant<T>
  else null

inline fun <reified T> QVariant_?.into(): T? =
  this?.withType<T>()?.value()

inline fun <reified T> QVariant_?.into(defValue: T): T =
  this?.withType<T>()?.value() ?: defValue

