package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test

class CharSerializerTest {
  @Test
  fun testAll() {
    assertEquals(' ', roundTrip(CharSerializer, ' '))
    assertEquals('a', roundTrip(CharSerializer, 'a'))
    assertEquals('ä', roundTrip(CharSerializer, 'ä'))
    assertEquals('\u0000', roundTrip(CharSerializer, '\u0000'))
    assertEquals('\uFFFF', roundTrip(CharSerializer, '\uFFFF'))
  }
}
