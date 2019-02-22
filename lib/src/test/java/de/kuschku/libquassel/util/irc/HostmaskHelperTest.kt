/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.util.irc

import org.junit.Assert.assertEquals
import org.junit.Test

class HostmaskHelperTest {
  @Test
  fun testNormal() {
    assertEquals("justJanne",
                 HostmaskHelper.nick("justJanne!kuschku@lithium.kuschku.de"))
    assertEquals("justJanne",
                 HostmaskHelper.split("justJanne!kuschku@lithium.kuschku.de").first)

    assertEquals("kuschku",
                 HostmaskHelper.user("justJanne!kuschku@lithium.kuschku.de"))
    assertEquals("kuschku",
                 HostmaskHelper.split("justJanne!kuschku@lithium.kuschku.de").second)

    assertEquals("lithium.kuschku.de",
                 HostmaskHelper.host("justJanne!kuschku@lithium.kuschku.de"))
    assertEquals("lithium.kuschku.de",
                 HostmaskHelper.split("justJanne!kuschku@lithium.kuschku.de").third)

    assertEquals("justJanne!kuschku@lithium.kuschku.de", HostmaskHelper.build(
      "justJanne", "kuschku", "lithium.kuschku.de"
    ))
  }

  @Test
  fun testUnvalidatedIdent() {
    assertEquals("justJanne",
                 HostmaskHelper.nick("justJanne!~kuschku@lithium.kuschku.de"))
    assertEquals("justJanne",
                 HostmaskHelper.split("justJanne!~kuschku@lithium.kuschku.de").first)

    assertEquals("~kuschku",
                 HostmaskHelper.user("justJanne!~kuschku@lithium.kuschku.de"))
    assertEquals("~kuschku",
                 HostmaskHelper.split("justJanne!~kuschku@lithium.kuschku.de").second)

    assertEquals("lithium.kuschku.de",
                 HostmaskHelper.host("justJanne!~kuschku@lithium.kuschku.de"))
    assertEquals("lithium.kuschku.de",
                 HostmaskHelper.split("justJanne!~kuschku@lithium.kuschku.de").third)

    assertEquals("justJanne!~kuschku@lithium.kuschku.de", HostmaskHelper.build(
      "justJanne", "~kuschku", "lithium.kuschku.de"
    ))
  }

  @Test
  fun testUnicode() {
    assertEquals("bärlauch",
                 HostmaskHelper.nick("bärlauch!maße@flüge.de"))
    assertEquals("bärlauch",
                 HostmaskHelper.split("bärlauch!maße@flüge.de").first)

    assertEquals("maße",
                 HostmaskHelper.user("bärlauch!maße@flüge.de"))
    assertEquals("maße",
                 HostmaskHelper.split("bärlauch!maße@flüge.de").second)

    assertEquals("flüge.de",
                 HostmaskHelper.host("bärlauch!maße@flüge.de"))
    assertEquals("flüge.de",
                 HostmaskHelper.split("bärlauch!maße@flüge.de").third)

    assertEquals("bärlauch!maße@flüge.de", HostmaskHelper.build(
      "bärlauch", "maße", "flüge.de"
    ))
  }

  @Test
  fun testServer() {
    assertEquals("irc.freenode.org",
                 HostmaskHelper.nick("irc.freenode.org"))
    assertEquals("irc.freenode.org",
                 HostmaskHelper.split("irc.freenode.org").first)

    assertEquals("",
                 HostmaskHelper.user("irc.freenode.org"))
    assertEquals("",
                 HostmaskHelper.split("irc.freenode.org").second)

    assertEquals("",
                 HostmaskHelper.host("irc.freenode.org"))
    assertEquals("",
                 HostmaskHelper.split("irc.freenode.org").third)

    assertEquals("irc.freenode.org", HostmaskHelper.build(
      "irc.freenode.org", "", ""
    ))
  }

  @Test
  fun testAtNick() {
    assertEquals("@nick",
                 HostmaskHelper.nick("@nick!~ident@example.org"))
    assertEquals("@nick",
                 HostmaskHelper.split("@nick!~ident@example.org").first)

    assertEquals("~ident",
                 HostmaskHelper.user("@nick!~ident@example.org"))
    assertEquals("~ident",
                 HostmaskHelper.split("@nick!~ident@example.org").second)

    assertEquals("example.org",
                 HostmaskHelper.host("@nick!~ident@example.org"))
    assertEquals("example.org",
                 HostmaskHelper.split("@nick!~ident@example.org").third)

    assertEquals("@nick!~ident@example.org", HostmaskHelper.build(
      "@nick", "~ident", "example.org"
    ))
  }

  @Test
  fun testReversedDelimiters() {
    assertEquals("a",
                 HostmaskHelper.nick("a@a!"))
    assertEquals("a",
                 HostmaskHelper.split("a@a!").first)

    assertEquals("",
                 HostmaskHelper.user("a@a!"))
    assertEquals("",
                 HostmaskHelper.split("a@a!").second)

    assertEquals("a!",
                 HostmaskHelper.host("a@a!"))
    assertEquals("a!",
                 HostmaskHelper.split("a@a!").third)

    assertEquals("a@a!", HostmaskHelper.build(
      "a", "", "a!"
    ))
  }

  @Test
  fun testDiscord() {
    assertEquals("Gin_",
                 HostmaskHelper.nick("Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord"))
    assertEquals("Gin_",
                 HostmaskHelper.split("Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord").first)

    assertEquals("Gin_!♡♅ƸӜƷ♅♡!",
                 HostmaskHelper.user("Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord"))
    assertEquals("Gin_!♡♅ƸӜƷ♅♡!",
                 HostmaskHelper.split("Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord").second)

    assertEquals("discord",
                 HostmaskHelper.host("Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord"))
    assertEquals("discord",
                 HostmaskHelper.split("Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord").third)

    assertEquals("Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord", HostmaskHelper.build(
      "Gin_", "Gin_!♡♅ƸӜƷ♅♡!", "discord"
    ))
  }
}
