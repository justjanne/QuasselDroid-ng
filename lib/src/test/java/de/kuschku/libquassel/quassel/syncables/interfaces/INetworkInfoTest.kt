package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test

class INetworkInfoTest {
  @Test
  fun testSerialization() {
    val original = INetwork.NetworkInfo(
      networkName = "QuakeNet",
      identity = 5,
      serverList = listOf(
        INetwork.Server(
          host = "irc.quakenet.org",
          port = 6667
        )
      )
    )
    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    assertEquals(original, copy)
  }

  @Test
  fun testCopy() {
    val original = INetwork.NetworkInfo(
      networkName = "QuakeNet",
      identity = 5,
      serverList = listOf(
        INetwork.Server(
          host = "irc.quakenet.org",
          port = 6667
        )
      )
    )
    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    assertEquals(original, copy)
  }
}
