package de.kuschku.justjanne.quasseldroid.util.irc

import irc.SenderColorUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SenderColorUtilTest {
  @Test
  fun verifyTestData() {
    assertEquals(0x5, SenderColorUtil.senderColor("mavhq"))
    assertEquals(0xa, SenderColorUtil.senderColor("winch"))
    assertEquals(0xc, SenderColorUtil.senderColor("mack"))
  }
}
