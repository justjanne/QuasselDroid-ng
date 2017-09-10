package de.kuschku.quasseldroid_ng.protocol.primitive.serializer

import de.kuschku.quasseldroid_ng.protocol.QVariant
import de.kuschku.quasseldroid_ng.protocol.Quassel_Features
import de.kuschku.quasseldroid_ng.util.helpers.copyTo
import de.kuschku.quasseldroid_ng.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder
import java.nio.charset.CharsetEncoder

abstract class StringSerializer(
  private val encoder: CharsetEncoder,
  private val decoder: CharsetDecoder,
  private val trailingNullBytes: Int
) : Serializer<String?> {
  constructor(charset: Charset,
              trailingNullByte: Boolean = false) : this(
    charset.newEncoder(),
    charset.newDecoder(),
    if (trailingNullByte) {
      1
    } else {
      0
    }
  )

  object UTF16 : StringSerializer(Charsets.UTF_16BE)
  object UTF8 : StringSerializer(Charsets.UTF_8)
  object C : StringSerializer(Charsets.ISO_8859_1, trailingNullByte = true)

  override fun serialize(buffer: ChainedByteBuffer, data: String?, features: Quassel_Features) {
    if (data == null) {
      IntSerializer.serialize(buffer, -1, features)
    } else {
      val charBuffer = CharBuffer.allocate(data.length)
      charBuffer.put(data)
      charBuffer.flip()
      val byteBuffer = encoder.encode(charBuffer)
      IntSerializer.serialize(buffer, byteBuffer.remaining() + trailingNullBytes, features)
      buffer.put(byteBuffer)
      for (i in 0 until trailingNullBytes)
        buffer.put(0)
    }
  }

  fun serialize(data: String?): ByteBuffer = if (data == null) {
    ByteBuffer.allocate(0)
  } else {
    val charBuffer = CharBuffer.allocate(data.length)
    charBuffer.put(data)
    charBuffer.flip()
    encoder.encode(charBuffer)
  }

  fun deserializeAll(buffer: ByteBuffer): String? {
    val len = buffer.remaining()
    return if (len == -1) {
      null
    } else {
      val byteBuffer = ByteBuffer.allocate(len)
      buffer.copyTo(byteBuffer)
      byteBuffer.clear()
      byteBuffer.limit(byteBuffer.limit() - trailingNullBytes)
      decoder.decode(byteBuffer).toString()
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): String? {
    val len = IntSerializer.deserialize(buffer, features)
    return if (len == -1) {
      null
    } else {
      val byteBuffer = ByteBuffer.allocate(len)
      buffer.copyTo(byteBuffer)
      byteBuffer.clear()
      byteBuffer.limit(byteBuffer.limit() - trailingNullBytes)
      decoder.decode(byteBuffer).toString()
    }
  }
}

fun QVariant<ByteBuffer>?.deserializeString(
  serializer: StringSerializer) = if (this?.data == null) {
  null
} else {
  serializer.deserializeAll(data)
}

fun ByteBuffer?.deserializeString(serializer: StringSerializer) = if (this == null) {
  null
} else {
  serializer.deserializeAll(this)
}

fun String?.serializeString(serializer: StringSerializer) = if (this == null) {
  null
} else {
  serializer.serialize(this)
}
