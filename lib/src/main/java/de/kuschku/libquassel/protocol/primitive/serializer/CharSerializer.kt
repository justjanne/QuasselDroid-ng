package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.nio.ChainedByteBuffer
import java.nio.ByteBuffer
import java.nio.CharBuffer

object CharSerializer : Serializer<Char> {
  private val byteBuffer = ByteBuffer.allocateDirect(2)
  private val charBuffer = CharBuffer.allocate(1)
  private val encoder = Charsets.UTF_16BE.newEncoder()
  private val decoder = Charsets.UTF_16BE.newDecoder()

  override fun serialize(buffer: ChainedByteBuffer, data: Char, features: Quassel_Features) {
    synchronized(this) {
      charBuffer.clear()
      charBuffer.put(data)
      charBuffer.flip()
      byteBuffer.clear()
      encoder.encode(charBuffer, byteBuffer, true)
      byteBuffer.flip()
      if (byteBuffer.remaining() == 2) {
        buffer.put(byteBuffer)
      } else {
        buffer.putShort(0)
      }
    }
  }

  override fun deserialize(buffer: ByteBuffer, features: Quassel_Features): Char {
    synchronized(this) {
      byteBuffer.clear()
      byteBuffer.putShort(buffer.short)
      byteBuffer.flip()
      charBuffer.clear()
      decoder.decode(byteBuffer, charBuffer, true)
      charBuffer.flip()
      return if (charBuffer.remaining() == 1)
        charBuffer.get()
      else
        '\u0000'
    }
  }
}
