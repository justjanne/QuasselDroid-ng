package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert
import org.junit.Test

class AliasManagerTest {
  @Test
  fun testSerialization() {
    val original = AliasManager(SignalProxy.NULL)
    original.setAliasList(original.defaults())

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    Assert.assertEquals(original, copy)
  }

  @Test
  fun testCopy() {
    val original = AliasManager(SignalProxy.NULL)
    original.setAliasList(original.defaults())

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    Assert.assertEquals(original, copy)
  }
}
