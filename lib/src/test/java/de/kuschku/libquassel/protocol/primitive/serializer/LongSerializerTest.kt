package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test

class LongSerializerTest {
  @Test
  fun testZero() {
    assertEquals(0L, roundTrip(LongSerializer, 0L))
  }

  @Test
  fun testMinimal() {
    assertEquals(Long.MIN_VALUE, roundTrip(LongSerializer, Long.MIN_VALUE))
  }

  @Test
  fun testMaximal() {
    assertEquals(Long.MAX_VALUE, roundTrip(LongSerializer, Long.MAX_VALUE))
  }

  @Test
  fun testAllOnes() {
    assertEquals(0L.inv(), roundTrip(LongSerializer, 0L.inv()))
  }
}
