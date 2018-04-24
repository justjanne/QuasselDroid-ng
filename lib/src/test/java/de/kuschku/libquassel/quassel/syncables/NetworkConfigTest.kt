package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.randomBoolean
import de.kuschku.libquassel.util.randomUInt
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert
import org.junit.Test

class NetworkConfigTest {
  @Test
  fun testSerialization() {
    val original = NetworkConfig(SignalProxy.NULL)
    original.setPingTimeoutEnabled(randomBoolean())
    original.setPingInterval(randomUInt())
    original.setMaxPingCount(randomUInt())
    original.setAutoWhoEnabled(randomBoolean())
    original.setAutoWhoInterval(randomUInt())
    original.setAutoWhoNickLimit(randomUInt())
    original.setAutoWhoDelay(randomUInt())
    original.setStandardCtcp(randomBoolean())

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    Assert.assertEquals(original, copy)
  }

  @Test
  fun testCopy() {
    val original = NetworkConfig(SignalProxy.NULL)
    original.setPingTimeoutEnabled(randomBoolean())
    original.setPingInterval(randomUInt())
    original.setMaxPingCount(randomUInt())
    original.setAutoWhoEnabled(randomBoolean())
    original.setAutoWhoInterval(randomUInt())
    original.setAutoWhoNickLimit(randomUInt())
    original.setAutoWhoDelay(randomUInt())
    original.setStandardCtcp(randomBoolean())

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    Assert.assertEquals(original, copy)
  }
}
