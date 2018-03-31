package de.kuschku.libquassel.protocol

import de.kuschku.libquassel.protocol.primitive.serializer.Serializer

sealed class QVariant<T> constructor(val data: T?, val type: Type, val serializer: Serializer<T>) {
  class Typed<T> internal constructor(data: T?, type: Type, serializer: Serializer<T>) :
    QVariant<T>(data, type, serializer) {
    override fun toString() = "QVariant.Typed(${type.serializableName}, $data})"
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Typed<*>) return false

      if (data != other.data) return false
      if (type != other.type) return false

      return true
    }

    override fun hashCode(): Int {
      var result = data?.hashCode() ?: 0
      result = 31 * result + type.hashCode()
      return result
    }
  }

  class Custom<T> internal constructor(data: T?, val qtype: QType, serializer: Serializer<T>) :
    QVariant<T>(data, qtype.type, serializer) {
    override fun toString() = "QVariant.Custom($qtype, $data)"
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Custom<*>) return false

      if (data != other.data) return false
      if (qtype != other.qtype) return false

      return true
    }

    override fun hashCode(): Int {
      var result = data?.hashCode() ?: 0
      result = 31 * result + qtype.hashCode()
      return result
    }
  }

  fun or(defValue: T): T {
    return data ?: defValue
  }

  companion object {
    fun <T> of(data: T?, type: Type): QVariant<T> {
      return QVariant.Typed(data, type, type.serializer as Serializer<T>)
    }

    fun <T> of(data: T?, type: QType) =
      QVariant.Custom(data, type, type.serializer as Serializer<T>)
  }
}

inline fun <reified U> QVariant_?.value(): U? = this?.value<U?>(null)

inline fun <reified U> QVariant_?.value(defValue: U): U = this?.data as? U ?: defValue

inline fun <reified U> QVariant_?.valueOr(f: () -> U): U = this?.data as? U ?: f()

inline fun <reified U> QVariant_?.valueOrThrow(e: Throwable = NullPointerException()): U =
  this?.data as? U ?: throw e

inline fun <reified U> QVariant_?.valueOrThrow(e: () -> Throwable): U =
  this?.data as? U ?: throw e()