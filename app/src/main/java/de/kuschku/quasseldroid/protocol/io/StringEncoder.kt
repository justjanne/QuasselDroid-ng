/*
 * Quasseldroid 1 Quassel client for Android
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

package de.kuschku.quasseldroid.protocol.io

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.CoderResult

class StringEncoder(charset: Charset) {
  private val encoder = charset.newEncoder()
  private val decoder = charset.newDecoder()
  private val charBuffer = CharBuffer.allocate(1024)

  private fun charBuffer(length: Int): CharBuffer =
    if (length < 1024) charBuffer
    else CharBuffer.allocate(length)

  private fun encodingLength(length: Int, nullLimited: Boolean) =
    if (nullLimited) length + 1
    else length

  private fun decodingLength(length: Int, nullLimited: Boolean) =
    if (nullLimited) length - 1
    else length

  fun encode(data: String?, target: ChainedByteBuffer, nullLimited: Boolean = false) {
    if (data == null) return

    val charBuffer = charBuffer(encodingLength(data.length, nullLimited))
    charBuffer.put(data)
    if (nullLimited) charBuffer.put(0.toChar())
    charBuffer.flip()
    encoder.reset()
    var result: CoderResult
    do {
      result = encoder.encode(charBuffer, target.nextBuffer(data.length), true)
    } while (result == CoderResult.OVERFLOW)
  }

  fun encode(data: String?, nullLimited: Boolean = false): ByteBuffer {
    if (data == null) return ByteBuffer.allocate(0)

    val charBuffer = charBuffer(encodingLength(data.length, nullLimited))
    charBuffer.put(data)
    if (nullLimited) charBuffer.put(0.toChar())
    charBuffer.flip()
    encoder.reset()
    return encoder.encode(charBuffer)
  }

  fun decode(source: ByteBuffer, length: Int, nullLimited: Boolean = false): String {
    val charBuffer = charBuffer(decodingLength(length, nullLimited))
    val oldlimit = source.limit()
    source.limit(decodingLength(source.position() + length, nullLimited))
    decoder.reset()
    decoder.decode(source, charBuffer, true)
    source.limit(oldlimit)
    charBuffer.flip()
    return charBuffer.toString()
  }

  fun decode(source: ByteBuffer, nullLimited: Boolean = false): String {
    val charBuffer = charBuffer(decodingLength(source.remaining(), nullLimited))
    source.limit(decodingLength(source.capacity(), nullLimited))
    decoder.reset()
    decoder.decode(source, charBuffer, true)
    charBuffer.flip()
    return charBuffer.toString()
  }
}
