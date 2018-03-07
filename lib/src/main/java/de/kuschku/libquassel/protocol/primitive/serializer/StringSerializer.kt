package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.helpers.hexDump
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder
import java.nio.charset.CharsetEncoder

abstract class StringSerializer(
  private var encoder: CharsetEncoder,
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

  private val charBuffer = ThreadLocal<CharBuffer>()

  object UTF16 : StringSerializer(Charsets.UTF_16BE)
  object UTF8 : StringSerializer(Charsets.UTF_8)
  object C : StringSerializer(Charsets.ISO_8859_1, trailingNullByte = true)

  private inline fun charBuffer(len: Int): CharBuffer {
    if (charBuffer.get() == null)
      charBuffer.set(CharBuffer.allocate(1024))
    val buf = if (len >= 1024)
      CharBuffer.allocate(len)
    else
      charBuffer.get()
    buf.clear()
    buf.limit(len)
    return buf
  }

  override fun serialize(buffer: ChainedByteBuffer, data: String?, features: Quassel_Features) {
    try {
      if (data == null) {
        IntSerializer.serialize(buffer, -1, features)
      } else {
        val charBuffer = charBuffer(data.length)
        charBuffer.put(data)
        charBuffer.flip()
        encoder = encoder.charset().newEncoder()
        val byteBuffer = encoder.encode(charBuffer)
        IntSerializer.serialize(buffer, byteBuffer.remaining() + trailingNullBytes, features)
        buffer.put(byteBuffer)
        for (i in 0 until trailingNullBytes)
          buffer.put(0)
      }
    } catch (e: Throwable) {
      throw RuntimeException(data, e)
    }
  }

  fun serialize(data: String?): ByteBuffer {
    try {
      if (data == null) {
        return ByteBuffer.allocate(0)
      } else {
        val charBuffer = charBuffer(data.length)
        charBuffer.put(data)
        charBuffer.flip()
        encoder = encoder.charset().newEncoder()
        return encoder.encode(charBuffer)
      }
    } catch (e: Throwable) {
      throw RuntimeException(data, e)
    }
  }

  fun deserializeAll(buffer: ByteBuffer): String? {
    try {
      val len = buffer.remaining()
      return if (len == -1) {
        null
      } else {
        val limit = buffer.limit()
        buffer.limit(buffer.position() + len - trailingNullBytes)
        val charBuffer = charBuffer(len)
        decoder.decode(buffer, charBuffer, true)
        buffer.limit(limit)
        buffer.position(buffer.position() + trailingNullBytes)
        charBuffer.flip()
        charBuffer.toString()
      }
    } catch (e: Throwable) {
      buffer.hexDump()
      throw RuntimeException(e)
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): String? {
    try {
      val len = IntSerializer.deserialize(buffer, features)
      return if (len == -1) {
        null
      } else {
        val limit = buffer.limit()
        buffer.limit(buffer.position() + Math.max(0, len - trailingNullBytes))
        val charBuffer = charBuffer(len)
        decoder.decode(buffer, charBuffer, true)
        buffer.limit(limit)
        buffer.position(buffer.position() + trailingNullBytes)
        charBuffer.flip()
        charBuffer.toString()
      }
    } catch (e: Throwable) {
      buffer.hexDump()
      throw RuntimeException(e)
    }
  }
}
