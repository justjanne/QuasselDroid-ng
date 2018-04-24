package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test

class BoolSerializerTest {
  @Test
  fun testFalse() {
    assertEquals(true, roundTrip(BoolSerializer, true))
    assertEquals(false, roundTrip(BoolSerializer, false))
  }
}
