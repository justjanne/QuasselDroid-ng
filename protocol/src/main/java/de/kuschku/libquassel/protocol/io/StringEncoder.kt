/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package de.kuschku.libquassel.protocol.io

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CoderResult

class StringEncoder(charset: Charset) {
  private val encoder = charset.newEncoder()
  private val decoder = charset.newDecoder()
  private val charBuffer = CharBuffer.allocate(1024)

  private fun charBuffer(length: Int): CharBuffer =
    if (length < 1024) charBuffer.clear()
    else CharBuffer.allocate(length)

  fun encode(data: String?, target: ChainedByteBuffer) {
    if (data == null) return

    val charBuffer = charBuffer(data.length)
    charBuffer.put(data)
    charBuffer.flip()
    encoder.reset()
    var result: CoderResult
    do {
      result = target.withBuffer(charBuffer.remaining()) {
        encoder.encode(charBuffer, it, true)
      }
    } while (result == CoderResult.OVERFLOW)
  }

  fun encode(data: String?): ByteBuffer {
    if (data == null) {
      return ByteBuffer.allocateDirect(0)
    }

    val charBuffer = charBuffer(data.length)
    charBuffer.put(data)
    charBuffer.flip()
    encoder.reset()
    return encoder.encode(charBuffer)
  }

  fun encodeChar(data: Char?, target: ChainedByteBuffer) {
    if (data == null) {
      target.putShort(0)
    } else {
      target.putChar(data)
    }
  }

  fun decode(source: ByteBuffer, length: Int): String {
    val charBuffer = charBuffer(length)
    val oldlimit = source.limit()
    source.limit(source.position() + length)
    decoder.reset()
    decoder.decode(source, charBuffer, true).also {
      if (it.isError) {
        source.position(source.position() + it.length())
      }
    }
    source.limit(oldlimit)
    charBuffer.flip()
    return charBuffer.toString()
  }

  fun decode(source: ByteBuffer): String {
    println("Called to decode ${source.contentToString()}")
    val charBuffer = charBuffer(source.remaining())
    decoder.reset()
    decoder.decode(source, charBuffer, true).also {
      if (it.isError) {
        println("Encountered error: $it")
        source.position(source.position() + it.length())
      }
    }
    charBuffer.flip()
    println("Result: $charBuffer")
    return charBuffer.toString()
  }

  fun decodeChar(source: ByteBuffer): Char {
    return source.getChar()
  }
}
