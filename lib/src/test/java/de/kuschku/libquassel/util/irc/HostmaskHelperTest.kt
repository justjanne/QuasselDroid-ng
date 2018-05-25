package de.kuschku.libquassel.util.irc

import org.junit.Assert.assertEquals
import org.junit.Test

class HostmaskHelperTest {
  @Test
  fun testNormal() {
    assertEquals("justJanne", HostmaskHelper.nick("justJanne!kuschku@lithium.kuschku.de"))
    assertEquals("kuschku", HostmaskHelper.user("justJanne!kuschku@lithium.kuschku.de"))
    assertEquals("lithium.kuschku.de", HostmaskHelper.host("justJanne!kuschku@lithium.kuschku.de"))
  }

  @Test
  fun testUnvalidatedIdent() {
    assertEquals("justJanne", HostmaskHelper.nick("justJanne!~kuschku@lithium.kuschku.de"))
    assertEquals("~kuschku", HostmaskHelper.user("justJanne!~kuschku@lithium.kuschku.de"))
    assertEquals("lithium.kuschku.de", HostmaskHelper.host("justJanne!~kuschku@lithium.kuschku.de"))
  }

  @Test
  fun testUnicode() {
    assertEquals("bärlauch", HostmaskHelper.nick("bärlauch!maße@flüge.de"))
    assertEquals("maße", HostmaskHelper.user("bärlauch!maße@flüge.de"))
    assertEquals("flüge.de", HostmaskHelper.host("bärlauch!maße@flüge.de"))
  }

  @Test
  fun testServer() {
    assertEquals("irc.freenode.org", HostmaskHelper.nick("irc.freenode.org"))
  }

  @Test
  fun testDiscord() {
    assertEquals("Gin_", HostmaskHelper.nick("Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord"))
    assertEquals("Gin_!♡♅ƸӜƷ♅♡!", HostmaskHelper.user("Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord"))
    assertEquals("discord", HostmaskHelper.host("Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord"))
  }
}
