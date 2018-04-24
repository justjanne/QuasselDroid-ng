package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test

class IntSerializerTest {
  @Test
  fun testZero() {
    assertEquals(0, roundTrip(IntSerializer, 0))
  }

  @Test
  fun testMinimal() {
    assertEquals(Int.MIN_VALUE, roundTrip(IntSerializer, Int.MIN_VALUE))
  }

  @Test
  fun testMaximal() {
    assertEquals(Int.MAX_VALUE, roundTrip(IntSerializer, Int.MAX_VALUE))
  }

  @Test
  fun testAllOnes() {
    assertEquals(0.inv(), roundTrip(IntSerializer, 0.inv()))
  }
}
