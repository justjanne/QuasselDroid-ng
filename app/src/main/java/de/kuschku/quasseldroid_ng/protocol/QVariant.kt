package de.kuschku.quasseldroid_ng.protocol

class QVariant<T>(val data: T?, val type: MetaType<T>) {
  constructor(data: T?, type: Type) : this(data, MetaType.Companion.get(type))
  constructor(data: T?, type: QType) : this(data, type.typeName)
  constructor(data: T?, type: String) : this(data, MetaType.Companion.get(type))

  private fun <U> coerce(): QVariant<U> {
    return this as QVariant<U>
  }

  fun or(defValue: T): T {
    return data ?: defValue
  }

  fun <U> _value(defValue: U): U {
    return this.coerce<U>().data ?: defValue
  }

  fun <U> _valueOr(f: () -> U): U {
    return this.coerce<U>().data ?: f()
  }

  fun <U> _valueOrThrow(): U = this._valueOrThrow(NullPointerException())

  fun <U> _valueOrThrow(e: Throwable): U {
    return this.coerce<U>().data ?: throw e
  }

  override fun toString(): String {
    return "QVariant(${type.name}, $data)"
  }
}

fun <U> QVariant_?.value(): U?
  = this?._value<U?>(null)

fun <U> QVariant_?.value(defValue: U): U
  = this?._value<U>(defValue) ?: defValue

fun <U> QVariant_?.valueOr(f: () -> U): U
  = this?._valueOr<U>(f) ?: f()

fun <U> QVariant_?.valueOrThrow(e: Throwable = NullPointerException()): U
  = this?._valueOrThrow<U>(e) ?: throw e
