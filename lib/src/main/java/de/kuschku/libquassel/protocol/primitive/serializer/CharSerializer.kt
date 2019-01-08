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

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer
import java.nio.CharBuffer

object CharSerializer : Serializer<Char> {
  private val byteBufferIn = ByteBuffer.allocateDirect(2)
  private val byteBufferOut = ByteBuffer.allocateDirect(2)
  private val charBufferIn = CharBuffer.allocate(1)
  private val charBufferOut = CharBuffer.allocate(1)
  private val encoder = Charsets.UTF_16BE.newEncoder()
  private val decoder = Charsets.UTF_16BE.newDecoder()
  override fun serialize(buffer: ChainedByteBuffer, data: Char, features: QuasselFeatures) {
    charBufferIn.clear()
    charBufferIn.put(data)
    charBufferIn.flip()
    byteBufferIn.clear()
    encoder.encode(charBufferIn, byteBufferIn, true)
    byteBufferIn.flip()
    if (byteBufferIn.remaining() == 2) {
      buffer.put(byteBufferIn)
    } else {
      buffer.putShort(0)
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: QuasselFeatures): Char {
    byteBufferOut.clear()
    byteBufferOut.putShort(buffer.short)
    byteBufferOut.flip()
    charBufferOut.clear()
    decoder.decode(byteBufferOut, charBufferOut, true)
    charBufferOut.flip()
    return if (charBufferOut.remaining() == 1)
      charBufferOut.get()
    else
      '\u0000'
  }
}
