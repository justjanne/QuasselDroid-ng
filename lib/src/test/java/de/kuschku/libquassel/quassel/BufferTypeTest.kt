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

package de.kuschku.libquassel.quassel

import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.util.flag.hasFlag
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BufferTypeTest {
  @Test
  fun testHasFlag() {
    assertTrue(Buffer_Type.of(Buffer_Type.StatusBuffer).hasFlag(Buffer_Type.StatusBuffer))
    assertFalse(Buffer_Type.of(Buffer_Type.StatusBuffer).hasFlag(Buffer_Type.QueryBuffer))
    assertFalse(Buffer_Type.of(Buffer_Type.StatusBuffer).hasFlag(Buffer_Type.ChannelBuffer))
    assertFalse(Buffer_Type.of(Buffer_Type.StatusBuffer).hasFlag(Buffer_Type.GroupBuffer))

    assertFalse(Buffer_Type.of(Buffer_Type.QueryBuffer).hasFlag(Buffer_Type.StatusBuffer))
    assertTrue(Buffer_Type.of(Buffer_Type.QueryBuffer).hasFlag(Buffer_Type.QueryBuffer))
    assertFalse(Buffer_Type.of(Buffer_Type.QueryBuffer).hasFlag(Buffer_Type.ChannelBuffer))
    assertFalse(Buffer_Type.of(Buffer_Type.QueryBuffer).hasFlag(Buffer_Type.GroupBuffer))

    assertFalse(Buffer_Type.of(Buffer_Type.ChannelBuffer).hasFlag(Buffer_Type.StatusBuffer))
    assertFalse(Buffer_Type.of(Buffer_Type.ChannelBuffer).hasFlag(Buffer_Type.QueryBuffer))
    assertTrue(Buffer_Type.of(Buffer_Type.ChannelBuffer).hasFlag(Buffer_Type.ChannelBuffer))
    assertFalse(Buffer_Type.of(Buffer_Type.ChannelBuffer).hasFlag(Buffer_Type.GroupBuffer))

    assertFalse(Buffer_Type.of(Buffer_Type.GroupBuffer).hasFlag(Buffer_Type.StatusBuffer))
    assertFalse(Buffer_Type.of(Buffer_Type.GroupBuffer).hasFlag(Buffer_Type.QueryBuffer))
    assertFalse(Buffer_Type.of(Buffer_Type.GroupBuffer).hasFlag(Buffer_Type.ChannelBuffer))
    assertTrue(Buffer_Type.of(Buffer_Type.GroupBuffer).hasFlag(Buffer_Type.GroupBuffer))
  }

  @Test
  fun testEnabledValues() {
    assertEquals(
      Buffer_Type.StatusBuffer,
      Buffer_Type.of(Buffer_Type.StatusBuffer).enabledValues().firstOrNull()
    )
    assertEquals(
      setOf(Buffer_Type.StatusBuffer),
      Buffer_Type.of(Buffer_Type.StatusBuffer).enabledValues()
    )

    assertEquals(
      Buffer_Type.QueryBuffer,
      Buffer_Type.of(Buffer_Type.QueryBuffer).enabledValues().firstOrNull()
    )
    assertEquals(
      setOf(Buffer_Type.QueryBuffer),
      Buffer_Type.of(Buffer_Type.QueryBuffer).enabledValues()
    )

    assertEquals(
      Buffer_Type.ChannelBuffer,
      Buffer_Type.of(Buffer_Type.ChannelBuffer).enabledValues().firstOrNull()
    )
    assertEquals(
      setOf(Buffer_Type.ChannelBuffer),
      Buffer_Type.of(Buffer_Type.ChannelBuffer).enabledValues()
    )

    assertEquals(
      Buffer_Type.GroupBuffer,
      Buffer_Type.of(Buffer_Type.GroupBuffer).enabledValues().firstOrNull()
    )
    assertEquals(
      setOf(Buffer_Type.GroupBuffer),
      Buffer_Type.of(Buffer_Type.GroupBuffer).enabledValues()
    )
  }
}
