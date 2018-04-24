package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test

class StringSerializerTest {
  @Test
  fun testAll() {
    assertEquals("Test", roundTrip(StringSerializer.UTF16, "Test"))
    assertEquals("Test", roundTrip(StringSerializer.UTF8, "Test"))
    assertEquals("Test", roundTrip(StringSerializer.C, "Test"))
  }
}
