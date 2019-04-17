/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

import de.kuschku.libquassel.util.helpers.sum
import java.io.Serializable

interface LongFlag<T> : Serializable where T : Enum<T>, T : LongFlag<T> {
  val bit: ULong
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

data class LongFlags<E>(
  val value: ULong,
  val values: Array<E>? = null
) : Number(), Serializable, Comparable<ULong> where E : Enum<E>, E : LongFlag<E> {
  override fun compareTo(other: ULong) = value.compareTo(other)

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
    is LongFlags<*> -> other.value == value
    is LongFlag<*>  -> other.bit == value
    else            -> other === this
  }

  override fun hashCode(): Int {
    return value.hashCode()
  }

  fun enabledValues() = values?.filter { hasFlag(it) }?.toSet() ?: emptySet()

  fun isEmpty() = value == 0uL
  fun isNotEmpty() = !isEmpty()

  override fun toString() = if (values != null) {
    enabledValues().joinToString("|", "[", "]")
  } else {
    value.toString(16)
  }

  companion object {
    inline fun <reified T> of(int: Long): LongFlags<T>
      where T : LongFlag<T>, T : Enum<T> =
      LongFlags(int.toULong(), enumValues())

    inline fun <reified T> of(int: ULong): LongFlags<T>
      where T : LongFlag<T>, T : Enum<T> =
      LongFlags(int, enumValues())

    inline fun <reified T> of(vararg flags: T): LongFlags<T>
      where T : LongFlag<T>, T : Enum<T> =
      LongFlags(flags.map(LongFlag<T>::bit).distinct().sum(), enumValues())

    inline fun <reified T> of(flags: Iterable<T>): LongFlags<T>
      where T : LongFlag<T>, T : Enum<T> =
      LongFlags(flags.map(LongFlag<T>::bit).distinct().sum(), enumValues())
  }

  interface Factory<E> where E : LongFlag<E>, E : Enum<E> {
    val NONE: LongFlags<E>
    fun of(bit: Long): LongFlags<E>
    fun of(bit: ULong): LongFlags<E>
    fun of(vararg flags: E): LongFlags<E>
    fun of(flags: Iterable<E>): LongFlags<E>
  }
}

infix fun <T> LongFlags<T>.hasFlag(which: T): Boolean where T : Enum<T>, T : LongFlag<T> {
  // an Undefined flag is a special case.
  if (which.bit == 0uL) return false
  return value and which.bit == which.bit
}

infix fun <T> LongFlags<T>.or(other: ULong): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value or other)

infix fun <T> LongFlags<T>.or(other: LongFlag<T>): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value or other.bit)

infix fun <T> LongFlags<T>.or(other: LongFlags<T>): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value or other.value)

infix fun <T> LongFlags<T>.and(other: ULong): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value and other)

infix fun <T> LongFlags<T>.and(other: LongFlag<T>): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value and other.bit)

infix fun <T> LongFlags<T>.and(other: LongFlags<T>): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value and other.value)

infix operator fun <T> LongFlags<T>.plus(other: ULong): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value or other)

infix operator fun <T> LongFlags<T>.plus(other: LongFlag<T>): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value or other.bit)

infix operator fun <T> LongFlags<T>.plus(other: LongFlags<T>): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value or other.value)

infix operator fun <T> LongFlags<T>.minus(other: ULong): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value and other.inv())

infix operator fun <T> LongFlags<T>.minus(other: LongFlag<T>): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value and other.bit.inv())

infix operator fun <T> LongFlags<T>.minus(other: LongFlags<T>): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value and other.value.inv())

infix fun <T> LongFlags<T>.unset(which: T): LongFlags<T>
  where T : Enum<T>, T : LongFlag<T> =
  LongFlags(value xor which.bit)
