/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.libquassel.util.flag

import de.kuschku.libquassel.util.helper.sum
import java.io.Serializable

interface ShortFlag<T> : Serializable where T : Enum<T>, T : ShortFlag<T> {
  val bit: UShort
  fun toByte() = bit.toByte()
  fun toChar() = bit.toLong().toChar()
  fun toDouble() = bit.toLong().toDouble()
  fun toFloat() = bit.toLong().toFloat()
  fun toInt() = bit.toInt()
  fun toLong() = bit.toLong()
  fun toShort() = bit.toShort()
  fun toUByte() = bit.toUByte()
  fun toUInt() = bit.toUInt()
  fun toULong() = bit.toULong()
  fun toUShort() = bit.toUShort()
}

data class ShortFlags<E>(
  val value: UShort,
  val values: Array<E>? = null
) : Number(), Serializable, Comparable<UShort> where E : Enum<E>, E : ShortFlag<E> {
  override fun compareTo(other: UShort) = value.compareTo(other)

  override fun toByte() = value.toByte()
  override fun toChar() = value.toLong().toChar()
  override fun toDouble() = value.toLong().toDouble()
  override fun toFloat() = value.toLong().toFloat()
  override fun toInt() = value.toInt()
  override fun toLong() = value.toLong()
  override fun toShort() = value.toShort()
  fun toUByte() = value.toUByte()
  fun toUInt() = value.toUInt()
  fun toULong() = value.toULong()
  fun toUShort() = value.toUShort()

  override fun equals(other: Any?) = when (other) {
    is ShortFlags<*> -> other.value == value
    is ShortFlag<*>  -> other.bit == value
    else             -> other === this
  }

  override fun hashCode(): Int {
    return value.hashCode()
  }

  fun enabledValues() = values?.filter { hasFlag(it) }?.toSet() ?: emptySet()

  fun isEmpty() = value == 0u.toUShort()
  fun isNotEmpty() = !isEmpty()

  override fun toString() = if (values != null) {
    enabledValues().joinToString("|", "[", "]")
  } else {
    value.toString(16)
  }

  companion object {
    inline fun <reified T> of(int: Short): ShortFlags<T>
      where T : ShortFlag<T>, T : Enum<T> =
      ShortFlags(int.toUShort(), enumValues())

    inline fun <reified T> of(int: UShort): ShortFlags<T>
      where T : ShortFlag<T>, T : Enum<T> =
      ShortFlags(int, enumValues())

    inline fun <reified T> of(vararg flags: T): ShortFlags<T>
      where T : ShortFlag<T>, T : Enum<T> =
      ShortFlags(flags.map(ShortFlag<T>::bit).distinct().sum().toUShort(), enumValues())

    inline fun <reified T> of(flags: Iterable<T>): ShortFlags<T>
      where T : ShortFlag<T>, T : Enum<T> =
      ShortFlags(flags.map(ShortFlag<T>::bit).distinct().sum().toUShort(), enumValues())
  }

  interface Factory<E> where E : ShortFlag<E>, E : Enum<E> {
    val NONE: ShortFlags<E>
    fun of(bit: Short): ShortFlags<E>
    fun of(bit: UShort): ShortFlags<E>
    fun of(vararg flags: E): ShortFlags<E>
    fun of(flags: Iterable<E>): ShortFlags<E>
  }
}

infix fun <T> ShortFlags<T>.hasFlag(which: T): Boolean where T : Enum<T>, T : ShortFlag<T> {
  // an Undefined flag is a special case.
  if (which.bit == 0u.toUShort()) return false
  return value and which.bit == which.bit
}

infix fun <T> ShortFlags<T>.or(other: UShort): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value or other)

infix fun <T> ShortFlags<T>.or(other: ShortFlag<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value or other.bit)

infix fun <T> ShortFlags<T>.or(other: ShortFlags<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value or other.value)

infix fun <T> ShortFlags<T>.and(other: UShort): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value and other)

infix fun <T> ShortFlags<T>.and(other: ShortFlag<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value and other.bit)

infix fun <T> ShortFlags<T>.and(other: ShortFlags<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value and other.value)

infix operator fun <T> ShortFlags<T>.plus(other: UShort): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value or other)

infix operator fun <T> ShortFlags<T>.plus(other: ShortFlag<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value or other.bit)

infix operator fun <T> ShortFlags<T>.plus(other: ShortFlags<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value or other.value)

infix operator fun <T> ShortFlags<T>.minus(other: UShort): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value and other.inv())

infix operator fun <T> ShortFlags<T>.minus(other: ShortFlag<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value and other.bit.inv())

infix operator fun <T> ShortFlags<T>.minus(other: ShortFlags<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value and other.value.inv())

infix fun <T> ShortFlags<T>.unset(which: T): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> =
  ShortFlags(value xor which.bit)
