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

package info.quasseldroid.protocol.variant

import info.quasseldroid.protocol.features.FeatureSet
import info.quasseldroid.protocol.io.ChainedByteBuffer
import info.quasseldroid.protocol.io.contentToString
import info.quasseldroid.protocol.serializers.QtSerializers
import info.quasseldroid.protocol.serializers.QuasselSerializers
import info.quasseldroid.protocol.serializers.primitive.QtSerializer
import info.quasseldroid.protocol.serializers.primitive.QuasselSerializer
import java.nio.ByteBuffer

typealias QVariant_ = QVariant<*>
typealias QVariantList = List<QVariant_>
typealias QVariantMap = Map<String, QVariant_>
typealias QStringList = List<String?>

sealed class QVariant<T> {
  abstract val data: T
  abstract val serializer: QtSerializer<T>

  data class Typed<T> internal constructor(
    override val data: T,
    override val serializer: QtSerializer<T>
  ) : QVariant<T>() {
    override fun toString() = super.toString()
  }

  data class Custom<T> internal constructor(
    override val data: T,
    override val serializer: QuasselSerializer<T>
  ) : QVariant<T>() {
    override fun toString() = super.toString()
  }

  fun value(): T = data


  fun serialize(buffer: ChainedByteBuffer, featureSet: FeatureSet) {
    serializer.serialize(buffer, data, featureSet)
  }

  override fun toString() = data.let {
    when (it) {
      is ByteBuffer ->
        "QVariant(${serializer::class.java.simpleName}, ${it.contentToString()})"
      else ->
        "QVariant(${serializer::class.java.simpleName}, $it)"
    }
  }

  @Suppress("UNCHECKED_CAST")
  inline fun <reified T> withType(): QVariant<T>? =
    if (serializer.javaType == T::class.java && this.value() is T) this as QVariant<T>
    else null

  companion object {
    fun <T> of(data: T, serializer: QtSerializer<T>) = Typed(data, serializer)
    fun <T> of(data: T, serializer: QuasselSerializer<T>) = Custom(data, serializer)
  }
}

inline fun <reified T> qVariant(data: T, type: QtType): QVariant<T> =
  QVariant.of(data, QtSerializers.find(type))

inline fun <reified T> qVariant(data: T, type: QuasselType): QVariant<T> =
  QVariant.of(data, QuasselSerializers.find(type))

inline fun <reified T> QVariant_?.into(): T? =
  this?.withType<T>()?.value()

inline fun <reified T> QVariant_?.into(defValue: T): T =
  this?.withType<T>()?.value() ?: defValue

