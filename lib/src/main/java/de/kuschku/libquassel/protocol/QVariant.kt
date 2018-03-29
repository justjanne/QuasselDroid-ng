package de.kuschku.libquassel.protocol

import de.kuschku.libquassel.protocol.primitive.serializer.Serializer
import java.nio.ByteBuffer
import java.nio.CharBuffer

sealed class QVariant<T> constructor(val data: T?, val type: Type, val serializer: Serializer<T>) {
  class Typed<T> internal constructor(data: T?, type: Type, serializer: Serializer<T>) :
    QVariant<T>(data, type, serializer) {
    override fun toString() = "QVariant.Typed(${type.serializableName}, ${toString(data)})"
  }

  class Custom<T> internal constructor(data: T?, val qtype: QType, serializer: Serializer<T>) :
    QVariant<T>(data, qtype.type, serializer) {
    override fun toString() = "QVariant.Custom($qtype, ${toString(data)})"
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

inline fun toString(data: Any?) = when (data) {
  is ByteBuffer -> data.array()?.contentToString()
  is CharBuffer -> data.array()?.contentToString()
  is Array<*>   -> data.contentToString()
  else          -> data.toString()
}

inline fun <reified U> QVariant_?.value(): U? = this?.value<U?>(null)

inline fun <reified U> QVariant_?.value(defValue: U): U = this?.data as? U ?: defValue

inline fun <reified U> QVariant_?.valueOr(f: () -> U): U = this?.data as? U ?: f()

inline fun <reified U> QVariant_?.valueOrThrow(e: Throwable = NullPointerException()): U =
  this?.data as? U ?: throw e

inline fun <reified U> QVariant_?.valueOrThrow(e: () -> Throwable): U =
  this?.data as? U ?: throw e()