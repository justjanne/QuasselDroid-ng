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

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.deserialize
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test

class BufferInfoSerializerTest {
  @Test
  fun testBaseCase() {
    val value = BufferInfo(
      BufferId(-1),
      NetworkId(-1),
      Buffer_Type.of(),
      -1,
      ""
    )
    assertEquals(value, roundTrip(BufferInfoSerializer, value))
    // @formatter:off
    assertEquals(value, deserialize(BufferInfoSerializer, byteArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1, 0, 0, 0, 0)))
    // @formatter:on
  }

  @Test
  fun testNormal() {
    val value = BufferInfo(
      BufferId.MAX_VALUE,
      NetworkId.MAX_VALUE,
      Buffer_Type.of(*Buffer_Type.validValues),
      Int.MAX_VALUE,
      "äẞ\u0000\uFFFF"
    )
    assertEquals(value, roundTrip(BufferInfoSerializer, value))
    // @formatter:off
    assertEquals(value, deserialize(BufferInfoSerializer, byteArrayOf(127, -1, -1, -1, 127, -1, -1, -1, 0, 15, 127, -1, -1, -1, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65)))
    // @formatter:on
  }
}
