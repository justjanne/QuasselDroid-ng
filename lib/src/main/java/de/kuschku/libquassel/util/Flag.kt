package de.kuschku.libquassel.util

interface Flag<T> where T : Enum<T>, T : Flag<T> {
  val bit: Int
  fun toByte() = bit.toByte()
  fun toChar() = bit.toChar()
  fun toDouble() = bit.toDouble()
  fun toFloat() = bit.toFloat()
  fun toInt() = bit
  fun toLong() = bit.toLong()
  fun toShort() = bit.toShort()
}

data class Flags<E>(
  val value: Int,
  val values: Array<E>? = null
) : Number(), Comparable<Int> where E : Enum<E>, E : Flag<E> {
  override fun compareTo(other: Int) = value.compareTo(other)
  override fun toByte() = value.toByte()
  override fun toChar() = value.toChar()
  override fun toDouble() = value.toDouble()
  override fun toFloat() = value.toFloat()
  override fun toInt() = value
  override fun toLong() = value.toLong()
  override fun toShort() = value.toShort()

  override fun equals(other: Any?) = when (other) {
    is Flags<*> -> other.value == value
    is Flag<*>  -> other.bit == value
    else        -> other === this
  }

  override fun hashCode(): Int {
    return value
  }

  fun enabledValues() = values?.filter { hasFlag(it) }?.toSet() ?: emptySet()

  fun isEmpty() = value == 0
  fun isNotEmpty() = !isEmpty()

  override fun toString() = if (values != null) {
    enabledValues().joinToString("|", "[", "]")
  } else {
    value.toString(16)
  }

  companion object {
    inline fun <reified T> of(int: Int): Flags<T>
      where T : Flag<T>, T : Enum<T> = Flags(int, enumValues())

    inline fun <reified T> of(vararg flags: Flag<T>): Flags<T>
      where T : Flag<T>, T : Enum<T> =
      Flags(flags.map(Flag<T>::bit).distinct().sum(), enumValues()
      )
  }

  interface Factory<E> where E : Flag<E>, E : Enum<E> {
    val NONE: Flags<E>
    fun of(bit: Int): Flags<E>
    fun of(vararg flags: E): Flags<E>
  }
}

infix fun <T> Flags<T>.hasFlag(which: T): Boolean where T : Enum<T>, T : Flag<T> {
  // an Undefined flag is a special case.
  if (value == 0 || (value > 0 && which.bit == 0)) return false

  return value and which.bit == which.bit
}

infix fun <T> Flags<T>.or(other: Int): Flags<T>
  where T : kotlin.Enum<T>, T : Flag<T> = Flags(value or other)

infix fun <T> Flags<T>.or(other: Flag<T>): Flags<T>
  where T : kotlin.Enum<T>, T : Flag<T> = Flags(value or other.bit)

infix fun <T> Flags<T>.or(other: Flags<T>): Flags<T>
  where T : kotlin.Enum<T>, T : Flag<T> = Flags(value or other.value)

infix fun <T> Flags<T>.and(other: Int): Flags<T>
  where T : kotlin.Enum<T>, T : Flag<T> = Flags(value and other)

infix fun <T> Flags<T>.and(other: Flag<T>): Flags<T>
  where T : kotlin.Enum<T>, T : Flag<T> = Flags(value and other.bit)

infix fun <T> Flags<T>.and(other: Flags<T>): Flags<T>
  where T : kotlin.Enum<T>, T : Flag<T> = Flags(value and other.value)

infix operator fun <T> Flags<T>.plus(other: Int): Flags<T>
  where T : Enum<T>, T : Flag<T> = Flags(value or other)

infix operator fun <T> Flags<T>.plus(other: Flag<T>): Flags<T>
  where T : Enum<T>, T : Flag<T> = Flags(value or other.bit)

infix operator fun <T> Flags<T>.plus(other: Flags<T>): Flags<T>
  where T : Enum<T>, T : Flag<T> = Flags(value or other.value)

infix operator fun <T> Flags<T>.minus(other: Int): Flags<T>
  where T : Enum<T>, T : Flag<T> = Flags(value and other.inv())

infix operator fun <T> Flags<T>.minus(other: Flag<T>): Flags<T>
  where T : Enum<T>, T : Flag<T> = Flags(value and other.bit.inv())

infix operator fun <T> Flags<T>.minus(other: Flags<T>): Flags<T>
  where T : Enum<T>, T : Flag<T> = Flags(value and other.value.inv())

infix fun <T> Flags<T>.unset(which: T): Flags<T>
  where T : Enum<T>, T : Flag<T> = Flags(value xor which.bit)
