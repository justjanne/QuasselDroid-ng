package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.Buffer_Activity
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.*
import org.junit.Assert
import org.junit.Test

class BufferViewConfigTest {
  @Test
  fun testSerialization() {
    val original = BufferViewConfig(randomUInt(), SignalProxy.NULL)
    original.setBufferViewName(randomString())
    original.setNetworkId(randomUInt())
    original.setAddNewBuffersAutomatically(randomBoolean())
    original.setSortAlphabetically(randomBoolean())
    original.setHideInactiveNetworks(randomBoolean())
    original.setHideInactiveNetworks(randomBoolean())
    original.setDisableDecoration(randomBoolean())
    original.setAllowedBufferTypes(Buffer_Type.of(*Buffer_Type.validValues))
    original.setMinimumActivity(randomOf(*Buffer_Activity.values()).toInt())
    original.setShowSearch(randomBoolean())

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    Assert.assertEquals(original, copy)
  }

  @Test
  fun testCopy() {
    val original = BufferViewConfig(randomUInt(), SignalProxy.NULL)
    original.setBufferViewName(randomString())
    original.setNetworkId(randomUInt())
    original.setAddNewBuffersAutomatically(randomBoolean())
    original.setSortAlphabetically(randomBoolean())
    original.setHideInactiveNetworks(randomBoolean())
    original.setHideInactiveNetworks(randomBoolean())
    original.setDisableDecoration(randomBoolean())
    original.setAllowedBufferTypes(Buffer_Type.of(*Buffer_Type.validValues))
    original.setMinimumActivity(randomOf(*Buffer_Activity.values()).toInt())
    original.setShowSearch(randomBoolean())

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    Assert.assertEquals(original, copy)
  }
}
