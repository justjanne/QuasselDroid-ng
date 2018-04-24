package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.quassel.syncables.interfaces.IHighlightRuleManager
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.randomBoolean
import de.kuschku.libquassel.util.randomOf
import de.kuschku.libquassel.util.randomString
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert
import org.junit.Test

class HighlightRuleManagerTest {
  @Test
  fun testSerialization() {
    val original = HighlightRuleManager(SignalProxy.NULL)
    original.setHighlightNick(randomOf(*IHighlightRuleManager.HighlightNickType.values()).value)
    original.setNicksCaseSensitive(randomBoolean())
    original.setHighlightRuleList(listOf(
      HighlightRuleManager.HighlightRule(
        randomString(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomString(),
        randomString()
      ),
      HighlightRuleManager.HighlightRule(
        randomString(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomString(),
        randomString()
      ),
      HighlightRuleManager.HighlightRule(
        randomString(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomString(),
        randomString()
      )
    ))

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    Assert.assertEquals(original, copy)
  }

  @Test
  fun testCopy() {
    val original = HighlightRuleManager(SignalProxy.NULL)
    original.setHighlightNick(randomOf(*IHighlightRuleManager.HighlightNickType.values()).value)
    original.setNicksCaseSensitive(randomBoolean())
    original.setHighlightRuleList(listOf(
      HighlightRuleManager.HighlightRule(
        randomString(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomString(),
        randomString()
      ),
      HighlightRuleManager.HighlightRule(
        randomString(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomString(),
        randomString()
      ),
      HighlightRuleManager.HighlightRule(
        randomString(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomBoolean(),
        randomString(),
        randomString()
      )
    ))

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    Assert.assertEquals(original, copy)
  }
}
