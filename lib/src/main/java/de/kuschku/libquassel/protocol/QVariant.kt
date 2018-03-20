package de.kuschku.libquassel.protocol

class QVariant<T>(val data: T?, val type: MetaType<T>) {
  constructor(data: T?, type: Type) : this(data, MetaType.Companion.get(type))
  constructor(data: T?, type: QType) : this(data, type.typeName)
  constructor(data: T?, type: String) : this(data, MetaType.Companion.get(type))

  fun or(defValue: T): T {
    return data ?: defValue
  }

  override fun toString(): String {
    return "QVariant(${type.name}, $data)"
  }
}

inline fun <reified U> QVariant_?.value(): U?
  = this?.value<U?>(null)

inline fun <reified U> QVariant_?.value(defValue: U): U = this?.data as? U ?: defValue

inline fun <reified U> QVariant_?.valueOr(f: () -> U): U = this?.data as? U ?: f()

inline fun <reified U> QVariant_?.valueOrThrow(e: Throwable = NullPointerException()): U =
  this?.data as? U ?: throw e

inline fun <reified U> QVariant_?.valueOrThrow(e: () -> Throwable): U =
  this?.data as? U ?: throw e()
