/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.libquassel.quassel

import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.util.flag.hasFlag
import org.junit.Assert
import org.junit.Test

class BufferTypeTest {
  @Test
  fun testHasFlag() {
    Assert.assertTrue(Buffer_Type.of(Buffer_Type.StatusBuffer).hasFlag(Buffer_Type.StatusBuffer))
    Assert.assertFalse(Buffer_Type.of(Buffer_Type.StatusBuffer).hasFlag(Buffer_Type.QueryBuffer))
    Assert.assertFalse(Buffer_Type.of(Buffer_Type.StatusBuffer).hasFlag(Buffer_Type.ChannelBuffer))
    Assert.assertFalse(Buffer_Type.of(Buffer_Type.StatusBuffer).hasFlag(Buffer_Type.GroupBuffer))

    Assert.assertFalse(Buffer_Type.of(Buffer_Type.QueryBuffer).hasFlag(Buffer_Type.StatusBuffer))
    Assert.assertTrue(Buffer_Type.of(Buffer_Type.QueryBuffer).hasFlag(Buffer_Type.QueryBuffer))
    Assert.assertFalse(Buffer_Type.of(Buffer_Type.QueryBuffer).hasFlag(Buffer_Type.ChannelBuffer))
    Assert.assertFalse(Buffer_Type.of(Buffer_Type.QueryBuffer).hasFlag(Buffer_Type.GroupBuffer))

    Assert.assertFalse(Buffer_Type.of(Buffer_Type.ChannelBuffer).hasFlag(Buffer_Type.StatusBuffer))
    Assert.assertFalse(Buffer_Type.of(Buffer_Type.ChannelBuffer).hasFlag(Buffer_Type.QueryBuffer))
    Assert.assertTrue(Buffer_Type.of(Buffer_Type.ChannelBuffer).hasFlag(Buffer_Type.ChannelBuffer))
    Assert.assertFalse(Buffer_Type.of(Buffer_Type.ChannelBuffer).hasFlag(Buffer_Type.GroupBuffer))

    Assert.assertFalse(Buffer_Type.of(Buffer_Type.GroupBuffer).hasFlag(Buffer_Type.StatusBuffer))
    Assert.assertFalse(Buffer_Type.of(Buffer_Type.GroupBuffer).hasFlag(Buffer_Type.QueryBuffer))
    Assert.assertFalse(Buffer_Type.of(Buffer_Type.GroupBuffer).hasFlag(Buffer_Type.ChannelBuffer))
    Assert.assertTrue(Buffer_Type.of(Buffer_Type.GroupBuffer).hasFlag(Buffer_Type.GroupBuffer))
  }

  @Test
  fun testEnabledValues() {
    Assert.assertEquals(
      Buffer_Type.StatusBuffer,
      Buffer_Type.of(Buffer_Type.StatusBuffer).enabledValues().firstOrNull()
    )
    Assert.assertEquals(
      setOf(Buffer_Type.StatusBuffer),
      Buffer_Type.of(Buffer_Type.StatusBuffer).enabledValues()
    )

    Assert.assertEquals(
      Buffer_Type.QueryBuffer,
      Buffer_Type.of(Buffer_Type.QueryBuffer).enabledValues().firstOrNull()
    )
    Assert.assertEquals(
      setOf(Buffer_Type.QueryBuffer),
      Buffer_Type.of(Buffer_Type.QueryBuffer).enabledValues()
    )

    Assert.assertEquals(
      Buffer_Type.ChannelBuffer,
      Buffer_Type.of(Buffer_Type.ChannelBuffer).enabledValues().firstOrNull()
    )
    Assert.assertEquals(
      setOf(Buffer_Type.ChannelBuffer),
      Buffer_Type.of(Buffer_Type.ChannelBuffer).enabledValues()
    )

    Assert.assertEquals(
      Buffer_Type.GroupBuffer,
      Buffer_Type.of(Buffer_Type.GroupBuffer).enabledValues().firstOrNull()
    )
    Assert.assertEquals(
      setOf(Buffer_Type.GroupBuffer),
      Buffer_Type.of(Buffer_Type.GroupBuffer).enabledValues()
    )
  }
}
