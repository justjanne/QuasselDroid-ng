package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.nio.ByteBuffer

class ByteArraySerializerTest {
  @Test
  fun testBaseCase() {
    val value = byteArrayOf()
    assertArrayEquals(value, roundTrip(ByteArraySerializer, ByteBuffer.wrap(value))?.array())
  }

  @Test
  fun testNormal() {
    val value = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    assertArrayEquals(value, roundTrip(ByteArraySerializer, ByteBuffer.wrap(value))?.array())
  }
}
