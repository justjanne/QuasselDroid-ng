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
