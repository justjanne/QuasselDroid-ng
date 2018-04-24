package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.randomString
import de.kuschku.libquassel.util.randomUInt
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert
import org.junit.Test

class IdentityTest {
  @Test
  fun testSerialization() {
    val original = Identity(SignalProxy.NULL)
    original.setId(randomUInt())
    original.setIdentityName(randomString())
    original.setRealName(randomString())
    original.setNicks(listOf(
      randomString(),
      randomString(),
      randomString()
    ))

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    Assert.assertEquals(original, copy)
  }

  @Test
  fun testCopy() {
    val original = Identity(SignalProxy.NULL)
    original.setId(randomUInt())
    original.setIdentityName(randomString())
    original.setRealName(randomString())
    original.setNicks(listOf(
      randomString(),
      randomString(),
      randomString()
    ))

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    Assert.assertEquals(original, copy)
  }
}
