package de.kuschku.libquassel.util

import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

interface ShortFlag<T> where T : Enum<T>, T : ShortFlag<T> {
  val bit: Short
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

infix fun <T> ShortFlags<T>.or(
  other: ShortFlag<T>): ShortFlags<T> where T : kotlin.Enum<T>, T : ShortFlag<T> = ShortFlags(
  value or other.bit)

operator infix fun <T> ShortFlags<T>.plus(
  other: ShortFlags<T>): ShortFlags<T>  where T : Enum<T>, T : ShortFlag<T> = ShortFlags(
  value or other.value)

operator infix fun <T> ShortFlags<T>.plus(
  other: ShortFlag<T>): ShortFlags<T>  where T : Enum<T>, T : ShortFlag<T> = ShortFlags(
  value or other.bit)

infix fun <T> ShortFlags<T>.unset(
  which: T): ShortFlags<T>  where T : Enum<T>, T : ShortFlag<T> = ShortFlags(
  value xor which.bit)
