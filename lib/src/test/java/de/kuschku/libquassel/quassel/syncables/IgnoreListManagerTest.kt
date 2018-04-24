package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.randomBoolean
import de.kuschku.libquassel.util.randomOf
import de.kuschku.libquassel.util.randomString
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert
import org.junit.Test

class IgnoreListManagerTest {
  @Test
  fun testSerialization() {
    val original = IgnoreListManager(ISession.NULL, SignalProxy.NULL)
    original.setIgnoreList(listOf(
      IgnoreListManager.IgnoreListItem(
        type = randomOf(*IgnoreListManager.IgnoreType.values()),
        ignoreRule = randomString(),
        isRegEx = randomBoolean(),
        strictness = randomOf(IgnoreListManager.StrictnessType.SoftStrictness,
                              IgnoreListManager.StrictnessType.HardStrictness),
        scope = randomOf(*IgnoreListManager.ScopeType.values()),
        scopeRule = randomString(),
        isActive = randomBoolean()
      ),
      IgnoreListManager.IgnoreListItem(
        type = randomOf(*IgnoreListManager.IgnoreType.values()),
        ignoreRule = randomString(),
        isRegEx = randomBoolean(),
        strictness = randomOf(IgnoreListManager.StrictnessType.SoftStrictness,
                              IgnoreListManager.StrictnessType.HardStrictness),
        scope = randomOf(*IgnoreListManager.ScopeType.values()),
        scopeRule = randomString(),
        isActive = randomBoolean()
      )
    ))

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    Assert.assertEquals(original, copy)
  }

  @Test
  fun testCopy() {
    val original = IgnoreListManager(ISession.NULL, SignalProxy.NULL)
    original.setIgnoreList(listOf(
      IgnoreListManager.IgnoreListItem(
        type = randomOf(*IgnoreListManager.IgnoreType.values()),
        ignoreRule = randomString(),
        isRegEx = randomBoolean(),
        strictness = randomOf(IgnoreListManager.StrictnessType.SoftStrictness,
                              IgnoreListManager.StrictnessType.HardStrictness),
        scope = randomOf(*IgnoreListManager.ScopeType.values()),
        scopeRule = randomString(),
        isActive = randomBoolean()
      ),
      IgnoreListManager.IgnoreListItem(
        type = randomOf(*IgnoreListManager.IgnoreType.values()),
        ignoreRule = randomString(),
        isRegEx = randomBoolean(),
        strictness = randomOf(IgnoreListManager.StrictnessType.SoftStrictness,
                              IgnoreListManager.StrictnessType.HardStrictness),
        scope = randomOf(*IgnoreListManager.ScopeType.values()),
        scopeRule = randomString(),
        isActive = randomBoolean()
      )
    ))

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    Assert.assertEquals(original, copy)
  }
}
