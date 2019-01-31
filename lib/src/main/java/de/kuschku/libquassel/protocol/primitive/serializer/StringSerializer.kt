/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
@file:Suppress("NOTHING_TO_INLINE")
package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.helpers.hexDump
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CharsetDecoder
import java.nio.charset.CharsetEncoder
import kotlin.concurrent.getOrSet

abstract class StringSerializer(
  private val charset: Charset,
  private val trailingNullBytes: Int
) : Serializer<String?> {
  constructor(charset: Charset, trailingNullByte: Boolean = false) :
    this(charset, if (trailingNullByte) 1 else 0)

  private val charBuffer = ThreadLocal<CharBuffer>()
  private val encoder = ThreadLocal<CharsetEncoder>()
  private val decoder = ThreadLocal<CharsetDecoder>()

  object UTF16 : StringSerializer(Charsets.UTF_16BE)
  object UTF8 : StringSerializer(Charsets.UTF_8)
  object C : StringSerializer(Charsets.ISO_8859_1, trailingNullByte = true)

  private inline fun charBuffer(len: Int): CharBuffer {
    val charBuffer = charBuffer.getOrSet {
      CharBuffer.allocate(1024)
    }

    val buf = if (len >= 1024)
      CharBuffer.allocate(len)
    else
      charBuffer
    buf.clear()
    buf.limit(len)
    return buf
  }

  private inline fun encoder() = encoder.getOrSet(charset::newEncoder)
  private inline fun decoder() = decoder.getOrSet(charset::newDecoder)

  override fun serialize(buffer: ChainedByteBuffer, data: String?, features: QuasselFeatures) =
    try {
      if (data == null) {
        IntSerializer.serialize(buffer, -1, features)
      } else {
        val charBuffer = charBuffer(data.length)
        charBuffer.put(data)
        charBuffer.flip()
        val encoder = encoder()
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

  fun serialize(data: String?): ByteBuffer = try {
    if (data == null) {
      ByteBuffer.allocate(0)
    } else {
      val charBuffer = charBuffer(data.length)
      charBuffer.put(data)
      charBuffer.flip()
      val encoder = encoder()
      encoder.reset()
      encoder.encode(charBuffer)
    }
  } catch (e: Throwable) {
    throw RuntimeException(data, e)
  }

  fun deserializeAll(buffer: ByteBuffer): String? = try {
    val len = buffer.remaining()
    if (len == -1) {
      null
    } else {
      val limit = buffer.limit()
      buffer.limit(buffer.position() + len - trailingNullBytes)
      val charBuffer = charBuffer(len)
      val decoder = decoder()
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

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): String? = try {
    val len = IntSerializer.deserialize(buffer, features)
    if (len == -1) {
      null
    } else {
      val limit = buffer.limit()
      buffer.limit(buffer.position() + Math.max(0, len - trailingNullBytes))
      val charBuffer = charBuffer(len)
      val decoder = decoder()
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
