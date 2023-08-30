/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HostmaskHelperTest {
  @Test
  fun testNormal() {
    runTest(
      source = "justJanne!kuschku@lithium.kuschku.de",
      nick = "justJanne",
      user = "kuschku",
      host = "lithium.kuschku.de"
    )
    assertEquals(
      "justJanne!kuschku@lithium.kuschku.de",
      HostmaskHelper.build(nick = "justJanne", user = "kuschku", host = "lithium.kuschku.de")
    )
  }

  @Test
  fun testUnvalidatedIdent() {
    runTest(
      source = "justJanne!~kuschku@lithium.kuschku.de",
      nick = "justJanne",
      user = "~kuschku",
      host = "lithium.kuschku.de"
    )
    assertEquals(
      "justJanne!~kuschku@lithium.kuschku.de",
      HostmaskHelper.build(nick = "justJanne", user = "~kuschku", host = "lithium.kuschku.de")
    )
  }

  @Test
  fun testUnicode() {
    runTest(
      source = "bärlauch!maße@flüge.de",
      nick = "bärlauch",
      user = "maße",
      host = "flüge.de"
    )
    assertEquals(
      "bärlauch!maße@flüge.de",
      HostmaskHelper.build(nick = "bärlauch", user = "maße", host = "flüge.de")
    )
  }

  @Test
  fun testServer() {
    runTest(
      source = "irc.freenode.org",
      nick = "irc.freenode.org",
      user = "",
      host = ""
    )
    assertEquals(
      "irc.freenode.org",
      HostmaskHelper.build(nick = "irc.freenode.org", user = "", host = "")
    )
  }

  @Test
  fun testAtNick() {
    runTest(
      source = "@nick!~ident@example.org",
      nick = "@nick",
      user = "~ident",
      host = "example.org"
    )
    assertEquals(
      "@nick!~ident@example.org",
      HostmaskHelper.build(nick = "@nick", user = "~ident", host = "example.org")
    )
  }

  @Test
  fun testReversedDelimiters() {
    runTest(
      source = "a@a!",
      nick = "a",
      user = "",
      host = "a!"
    )
    assertEquals(
      "a@a!",
      HostmaskHelper.build(nick = "a", user = "", host = "a!")
    )
  }

  @Test
  fun testDiscord() {
    runTest(
      source = "Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord",
      nick = "Gin_",
      user = "Gin_!♡♅ƸӜƷ♅♡!",
      host = "discord"
    )
    assertEquals(
      "Gin_!Gin_!♡♅ƸӜƷ♅♡!@discord",
      HostmaskHelper.build(nick = "Gin_", user = "Gin_!♡♅ƸӜƷ♅♡!", host = "discord")
    )
  }

  @Test
  fun testDan() {
    runTest(
      source = "coolguy!ag@127.0.0.1",
      nick = "coolguy",
      user = "ag",
      host = "127.0.0.1"
    )

    runTest(
      source = "coolguy!~ag@localhost",
      nick = "coolguy",
      user = "~ag",
      host = "localhost"
    )
  }

  @Test
  fun testDanMissingAtoms() {
    runTest(
      source = "!ag@127.0.0.1",
      nick = "",
      user = "ag",
      host = "127.0.0.1"
    )

    runTest(
      source = "coolguy!@127.0.0.1",
      nick = "coolguy",
      user = "",
      host = "127.0.0.1"
    )

    runTest(
      source = "coolguy@127.0.0.1",
      nick = "coolguy",
      user = "",
      host = "127.0.0.1"
    )

    runTest(
      source = "coolguy!ag@",
      nick = "coolguy",
      user = "ag",
      host = ""
    )

    runTest(
      source = "coolguy!ag",
      nick = "coolguy",
      user = "ag",
      host = ""
    )
  }

  @Test
  fun testDanWeirdControlCodes() {
    runTest(
      source = "coolguy!ag@net\u00035w\u0003ork.admin",
      nick = "coolguy",
      user = "ag",
      host = "net\u00035w\u0003ork.admin"
    )

    runTest(
      source = "coolguy!~ag@n\u0002et\u000305w\u000fork.admin",
      nick = "coolguy",
      user = "~ag",
      host = "n\u0002et\u000305w\u000fork.admin"
    )
  }

  private fun runTest(source: String, nick: String, user: String, host: String) {
    assertEquals(nick, HostmaskHelper.nick(source))
    assertEquals(nick, HostmaskHelper.split(source).first)

    assertEquals(user, HostmaskHelper.user(source))
    assertEquals(user, HostmaskHelper.split(source).second)

    assertEquals(host, HostmaskHelper.host(source))
    assertEquals(host, HostmaskHelper.split(source).third)
  }
}
