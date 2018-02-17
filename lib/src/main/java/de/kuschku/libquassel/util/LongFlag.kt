package de.kuschku.libquassel.util

interface LongFlag<T> where T : Enum<T>, T : LongFlag<T> {
  val bit: Long
  fun toByte() = bit.toByte()
  fun toChar() = bit.toChar()
  fun toDouble() = bit.toDouble()
  fun toFloat() = bit.toFloat()
  fun toInt() = bit.toInt()
  fun toLong() = bit
  fun toShort() = bit.toShort()
}

data class LongFlags<E>(
  val value: Long,
  val values: Array<E>? = null
) : Number(), Comparable<Long> where E : Enum<E>, E : LongFlag<E> {
  override fun compareTo(other: Long) = value.compareTo(other)
  override fun toByte() = value.toByte()
  override fun toChar() = value.toChar()
  override fun toDouble() = value.toDouble()
  override fun toFloat() = value.toFloat()
  override fun toInt() = value.toInt()
  override fun toLong() = value
  override fun toShort() = value.toShort()

  override fun equals(other: Any?) = when (other) {
    is LongFlags<*> -> other.value == value
    is LongFlag<*>  -> other.bit == value
    else            -> other === this
  }

  override fun hashCode(): Int {
    return value.toInt()
  }

  fun enabledValues() = values?.filter { hasFlag(it) }?.toSet() ?: emptySet()

  fun isEmpty() = value == 0L
  fun isNotEmpty() = !isEmpty()

  override fun toString() = if (values != null) {
    enabledValues().joinToString("|", "[", "]")
  } else {
    value.toString(16)
  }

  companion object {
    inline fun <reified T> of(int: Long): LongFlags<T> where T : LongFlag<T>, T : Enum<T>
      = LongFlags(int, enumValues())

    inline fun <reified T> of(
      vararg flags: LongFlag<T>): LongFlags<T> where T : LongFlag<T>, T : Enum<T>
      = LongFlags(flags.map(LongFlag<T>::bit).distinct().sum(), enumValues())
  }

  interface Factory<E> where E : LongFlag<E>, E : Enum<E> {
    fun of(bit: Long): LongFlags<E>
    fun of(vararg flags: E): LongFlags<E>
  }
}

infix fun <T> LongFlags<T>.hasFlag(which: T): Boolean where T : Enum<T>, T : LongFlag<T> {
  // an Undefined flag is a special case.
  if (value == 0.toLong() || (value > 0 && which.bit == 0.toLong())) return false

  return value and which.bit == which.bit
}

infix fun <T> LongFlags<T>.or(
  other: LongFlag<T>): LongFlags<T> where T : kotlin.Enum<T>, T : LongFlag<T> = LongFlags(
  value or other.bit
)

infix fun <T> LongFlags<T>.or(
  other: LongFlags<T>): LongFlags<T> where T : kotlin.Enum<T>, T : LongFlag<T> = LongFlags(
  value or other.value
)

infix fun <T> LongFlags<T>.and(
  other: LongFlag<T>): LongFlags<T> where T : kotlin.Enum<T>, T : LongFlag<T> = LongFlags(
  value and other.bit
)

infix fun <T> LongFlags<T>.and(
  other: LongFlags<T>): LongFlags<T> where T : kotlin.Enum<T>, T : LongFlag<T> = LongFlags(
  value and other.value
)

operator infix fun <T> LongFlags<T>.plus(
  other: LongFlags<T>): LongFlags<T>  where T : Enum<T>, T : LongFlag<T> = LongFlags(
  value or other.value
)

operator infix fun <T> LongFlags<T>.plus(
  other: LongFlag<T>): LongFlags<T>  where T : Enum<T>, T : LongFlag<T> = LongFlags(
  value or other.bit
)

infix fun <T> LongFlags<T>.unset(
  which: T): LongFlags<T>  where T : Enum<T>, T : LongFlag<T> = LongFlags(
  value xor which.bit
)
