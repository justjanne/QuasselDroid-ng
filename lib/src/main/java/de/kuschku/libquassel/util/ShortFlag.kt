package de.kuschku.libquassel.util

import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or
import kotlin.experimental.xor

interface ShortFlag<T> where T : Enum<T>, T : ShortFlag<T> {
  val bit: Short
  fun toByte() = bit.toByte()
  fun toChar() = bit.toChar()
  fun toDouble() = bit.toDouble()
  fun toFloat() = bit.toFloat()
  fun toInt() = bit.toInt()
  fun toLong() = bit.toLong()
  fun toShort() = bit
}

data class ShortFlags<E>(
  val value: Short,
  val values: Array<E>? = null
) : Number(), Comparable<Short> where E : Enum<E>, E : ShortFlag<E> {
  override fun compareTo(other: Short) = value.compareTo(other)
  override fun toByte() = value.toByte()
  override fun toChar() = value.toChar()
  override fun toDouble() = value.toDouble()
  override fun toFloat() = value.toFloat()
  override fun toInt() = value.toInt()
  override fun toLong() = value.toLong()
  override fun toShort() = value

  override fun equals(other: Any?) = when (other) {
    is ShortFlags<*> -> other.value == value
    is ShortFlag<*>  -> other.bit == value
    else             -> other === this
  }

  override fun hashCode(): Int {
    return value.toInt()
  }

  fun enabledValues() = values?.filter { hasFlag(it) }?.toSet() ?: emptySet()

  fun isEmpty() = value == 0.toShort()
  fun isNotEmpty() = !isEmpty()

  override fun toString() = if (values != null) {
    enabledValues().joinToString("|", "[", "]")
  } else {
    value.toString(16)
  }

  companion object {
    inline fun <reified T> ofBitMask(int: Short): ShortFlags<T> where T : ShortFlag<T>, T : Enum<T>
      = ShortFlags(int, enumValues())

    inline fun <reified T> of(
      vararg flags: ShortFlag<T>): ShortFlags<T> where T : ShortFlag<T>, T : Enum<T>
      = ShortFlags(flags.map(ShortFlag<T>::bit).distinct().sum().toShort(), enumValues())
  }

  interface Factory<E> where E : ShortFlag<E>, E : Enum<E> {
    fun of(bit: Short): ShortFlags<E>
    fun of(vararg flags: E): ShortFlags<E>
  }
}

infix fun <T> ShortFlags<T>.hasFlag(which: T): Boolean where T : Enum<T>, T : ShortFlag<T> {
  // an Undefined flag is a special case.
  if (value == 0.toShort() || (value > 0 && which.bit == 0.toShort())) return false

  return value and which.bit == which.bit
}

infix fun <T> ShortFlags<T>.or(other: Short): ShortFlags<T>
  where T : kotlin.Enum<T>, T : ShortFlag<T> = ShortFlags(value or other)

infix fun <T> ShortFlags<T>.or(other: ShortFlag<T>): ShortFlags<T>
  where T : kotlin.Enum<T>, T : ShortFlag<T> = ShortFlags(value or other.bit)

infix fun <T> ShortFlags<T>.or(other: ShortFlags<T>): ShortFlags<T>
  where T : kotlin.Enum<T>, T : ShortFlag<T> = ShortFlags(value or other.value)

infix fun <T> ShortFlags<T>.and(other: Short): ShortFlags<T>
  where T : kotlin.Enum<T>, T : ShortFlag<T> = ShortFlags(value and other)

infix fun <T> ShortFlags<T>.and(other: ShortFlag<T>): ShortFlags<T>
  where T : kotlin.Enum<T>, T : ShortFlag<T> = ShortFlags(value and other.bit)

infix fun <T> ShortFlags<T>.and(other: ShortFlags<T>): ShortFlags<T>
  where T : kotlin.Enum<T>, T : ShortFlag<T> = ShortFlags(value and other.value)

infix operator fun <T> ShortFlags<T>.plus(other: Short): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> = ShortFlags(value or other)

infix operator fun <T> ShortFlags<T>.plus(other: ShortFlag<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> = ShortFlags(value or other.bit)

infix operator fun <T> ShortFlags<T>.plus(other: ShortFlags<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> = ShortFlags(value or other.value)

infix operator fun <T> ShortFlags<T>.minus(other: Short): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> = ShortFlags(value and other.inv())

infix operator fun <T> ShortFlags<T>.minus(other: ShortFlag<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> = ShortFlags(value and other.bit.inv())

infix operator fun <T> ShortFlags<T>.minus(other: ShortFlags<T>): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> = ShortFlags(value and other.value.inv())

infix fun <T> ShortFlags<T>.unset(which: T): ShortFlags<T>
  where T : Enum<T>, T : ShortFlag<T> = ShortFlags(value xor which.bit)