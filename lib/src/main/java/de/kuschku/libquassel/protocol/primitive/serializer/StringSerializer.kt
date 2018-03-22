package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.helpers.hexDump
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder
import java.nio.charset.CharsetEncoder
import java.util.concurrent.atomic.AtomicReference

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

  private val thread = AtomicReference<Thread>()
  private val charBuffer = CharBuffer.allocate(1024)

  object UTF16 : StringSerializer(Charsets.UTF_16BE)
  object UTF8 : StringSerializer(Charsets.UTF_8)
  object C : StringSerializer(Charsets.ISO_8859_1, trailingNullByte = true)

  private inline fun charBuffer(len: Int): CharBuffer {
    val buf = if (len >= 1024)
      CharBuffer.allocate(len)
    else
      charBuffer
    buf.clear()
    buf.limit(len)
    return buf
  }

  private inline fun <T> preventThreadRaces(f: () -> T): T {
    val currentThread = Thread.currentThread()
    if (!thread.compareAndSet(null, currentThread)) {
      throw RuntimeException("Illegal Thread access!")
    }
    val result: T = f()
    if (!thread.compareAndSet(currentThread, null)) {
      throw RuntimeException("Illegal Thread access!")
    }
    return result
  }

  override fun serialize(buffer: ChainedByteBuffer, data: String?, features: Quassel_Features) =
    preventThreadRaces {
      try {
        if (data == null) {
          IntSerializer.serialize(buffer, -1, features)
        } else {
          val charBuffer = charBuffer(data.length)
          charBuffer.put(data)
          charBuffer.flip()
          encoder.reset()
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

  fun serialize(data: String?): ByteBuffer = preventThreadRaces {
    try {
      if (data == null) {
        ByteBuffer.allocate(0)
      } else {
        val charBuffer = charBuffer(data.length)
        charBuffer.put(data)
        charBuffer.flip()
        encoder.reset()
        encoder.encode(charBuffer)
      }
    } catch (e: Throwable) {
      throw RuntimeException(data, e)
    }
  }

  fun deserializeAll(buffer: ByteBuffer): String? = preventThreadRaces {
    try {
      val len = buffer.remaining()
      if (len == -1) {
        null
      } else {
        val limit = buffer.limit()
        buffer.limit(buffer.position() + len - trailingNullBytes)
        val charBuffer = charBuffer(len)
        decoder.reset()
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

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): String? =
    preventThreadRaces {
      try {
        val len = IntSerializer.deserialize(buffer, features)
        if (len == -1) {
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
