/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package de.kuschku.libquassel.protocol.types

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SignedIdTest {
  @Test
  fun testNegativeOne() {
    assertFalse(BufferId(-1).isValid())
    assertFalse(IdentityId(-1).isValid())
    assertFalse(MsgId(-1).isValid())
    assertFalse(NetworkId(-1).isValid())
  }

  @Test
  fun testZero() {
    assertFalse(BufferId(0).isValid())
    assertFalse(IdentityId(0).isValid())
    assertFalse(MsgId(0).isValid())
    assertFalse(NetworkId(0).isValid())
  }

  @Test
  fun testMinimal() {
    assertFalse(BufferId(Int.MIN_VALUE).isValid())
    assertFalse(IdentityId(Int.MIN_VALUE).isValid())
    assertFalse(MsgId(Long.MIN_VALUE).isValid())
    assertFalse(NetworkId(Int.MIN_VALUE).isValid())
  }

  @Test
  fun testMaximum() {
    assertTrue(BufferId(Int.MAX_VALUE).isValid())
    assertTrue(IdentityId(Int.MAX_VALUE).isValid())
    assertTrue(MsgId(Long.MAX_VALUE).isValid())
    assertTrue(NetworkId(Int.MAX_VALUE).isValid())
  }

  @Test
  fun testSortOrder() {
    assertEquals(
      listOf(
        BufferId(Int.MIN_VALUE),
        BufferId(-1),
        BufferId(0),
        BufferId(Int.MAX_VALUE)
      ),
      listOf(
        BufferId(Int.MAX_VALUE),
        BufferId(Int.MIN_VALUE),
        BufferId(0),
        BufferId(-1)
      ).sorted()
    )

    assertEquals(
      listOf(
        IdentityId(Int.MIN_VALUE),
        IdentityId(-1),
        IdentityId(0),
        IdentityId(Int.MAX_VALUE)
      ),
      listOf(
        IdentityId(Int.MAX_VALUE),
        IdentityId(Int.MIN_VALUE),
        IdentityId(0),
        IdentityId(-1)
      ).sorted()
    )

    assertEquals(
      listOf(
        MsgId(Long.MIN_VALUE),
        MsgId(-1),
        MsgId(0),
        MsgId(Long.MAX_VALUE)
      ),
      listOf(
        MsgId(Long.MAX_VALUE),
        MsgId(Long.MIN_VALUE),
        MsgId(0),
        MsgId(-1)
      ).sorted()
    )

    assertEquals(
      listOf(
        NetworkId(Int.MIN_VALUE),
        NetworkId(-1),
        NetworkId(0),
        NetworkId(Int.MAX_VALUE)
      ),
      listOf(
        NetworkId(Int.MAX_VALUE),
        NetworkId(Int.MIN_VALUE),
        NetworkId(0),
        NetworkId(-1)
      ).sorted()
    )
  }
}
