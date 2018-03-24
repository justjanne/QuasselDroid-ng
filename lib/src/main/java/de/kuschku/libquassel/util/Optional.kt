package de.kuschku.libquassel.util

import java.io.Serializable

interface Optional<T> : Serializable {
  fun get(): T
  fun isPresent(): Boolean
  fun ifPresent(consumer: (T) -> Unit)
  fun filter(predicate: (T) -> Boolean): Optional<T>
  fun <U> map(mapper: (T) -> U): Optional<U>
  fun <U> flatMap(mapper: (T) -> Optional<U>): Optional<U>
  fun orElse(other: T): T
  fun orNull(): T?
  fun <X : Throwable> orElseThrow(supplier: () -> X): T
  override fun equals(other: Any?): Boolean
  override fun hashCode(): Int
  override fun toString(): String

  private class Present<T>(private val value: T) : Optional<T> {
    override fun get() = value
    override fun isPresent() = true
    override fun ifPresent(consumer: (T) -> Unit) = consumer(value)
    override fun filter(predicate: (T) -> Boolean) = if (predicate(value)) this else empty<T>()
    override fun <U> map(mapper: (T) -> U) = ofNullable(mapper(value))
    override fun <U> flatMap(mapper: (T) -> Optional<U>) = mapper(value)
    override fun orElse(other: T) = value
    override fun orNull(): T? = value
    override fun <X : Throwable> orElseThrow(supplier: () -> X) = value
    override fun equals(other: Any?) = (other as? Present<*>)?.value == value
    override fun hashCode() = value?.hashCode() ?: 0
    override fun toString() = "Optional[$value]"
  }

  private class Absent<T> : Optional<T> {
    override fun get() = throw NoSuchElementException("No value present")
    override fun isPresent() = false
    override fun ifPresent(consumer: (T) -> Unit) = Unit
    override fun filter(predicate: (T) -> Boolean) = this
    override fun <U> map(mapper: (T) -> U) = empty<U>()
    override fun <U> flatMap(mapper: (T) -> Optional<U>) = empty<U>()
    override fun orElse(other: T) = other
    override fun orNull(): T? = null
    override fun <X : Throwable> orElseThrow(supplier: () -> X) = throw supplier()
    override fun equals(other: Any?) = other === this
    override fun hashCode() = 0
    override fun toString() = "Optional.empty"
  }

  companion object {
    private val absent = Absent<Any?>()

    @Suppress("UNCHECKED_CAST")
    fun <T> empty(): Optional<T> = absent as Absent<T>

    fun <T> of(value: T): Optional<T> = Present(value)
    fun <T> ofNullable(value: T?): Optional<T> = value?.let(::Present) ?: empty()
  }
}