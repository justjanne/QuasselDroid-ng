package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.experimental.inv

class ShortSerializerTest {
  @Test
  fun testZero() {
    assertEquals(0.toShort(), roundTrip(ShortSerializer, 0.toShort()))
  }

  @Test
  fun testMinimal() {
    assertEquals(Short.MIN_VALUE, roundTrip(ShortSerializer, Short.MIN_VALUE))
  }

  @Test
  fun testMaximal() {
    assertEquals(Short.MAX_VALUE, roundTrip(ShortSerializer, Short.MAX_VALUE))
  }

  @Test
  fun testAllOnes() {
    assertEquals((0.toShort().inv()), roundTrip(ShortSerializer, (0.toShort().inv())))
  }
}
