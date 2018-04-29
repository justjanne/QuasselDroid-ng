/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test

class BufferInfoSerializerTest {
  @Test
  fun testBaseCase() {
    val value = BufferInfo(
      -1,
      -1,
      Buffer_Type.of(),
      -1,
      ""
    )
    assertEquals(value, roundTrip(BufferInfoSerializer, value))
  }

  @Test
  fun testNormal() {
    val value = BufferInfo(
      Int.MAX_VALUE,
      Int.MAX_VALUE,
      Buffer_Type.of(*Buffer_Type.validValues),
      Int.MAX_VALUE,
      "äẞ\u0000\uFFFF"
    )
    assertEquals(value, roundTrip(BufferInfoSerializer, value))
  }
}
