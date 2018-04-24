package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.experimental.inv

class ByteSerializerTest {
  @Test
  fun testZero() {
    assertEquals(0.toByte(), roundTrip(ByteSerializer, 0.toByte()))
  }

  @Test
  fun testMinimal() {
    assertEquals(Byte.MIN_VALUE, roundTrip(ByteSerializer, Byte.MIN_VALUE))
  }

  @Test
  fun testMaximal() {
    assertEquals(Byte.MAX_VALUE, roundTrip(ByteSerializer, Byte.MAX_VALUE))
  }

  @Test
  fun testAllOnes() {
    assertEquals((0.toByte().inv()), roundTrip(ByteSerializer, (0.toByte().inv())))
  }
}
