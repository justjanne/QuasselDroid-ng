package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert
import org.junit.Test

class BufferInfoSerializerTest {
  @Test
  fun testBaseCase() {
    val value = BufferInfo(
      -1,
      -1,
      Buffer_Type.of(),
      -1,
      ""
    )
    Assert.assertEquals(value, roundTrip(BufferInfoSerializer, value))
  }

  @Test
  fun testNormal() {
    val value = BufferInfo(
      Int.MAX_VALUE,
      Int.MAX_VALUE,
      Buffer_Type.of(*Buffer_Type.validValues),
      Int.MAX_VALUE,
      "äẞ\u0000\uFFFF"
    )
    Assert.assertEquals(value, roundTrip(BufferInfoSerializer, value))
  }
}
