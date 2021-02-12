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
package info.quasseldroid.protocol.serializers.primitive

import de.justjanne.bitflags.none
import de.justjanne.bitflags.validValues
import info.quasseldroid.protocol.serializers.QuasselSerializers
import info.quasseldroid.protocol.testutil.byteBufferOf
import info.quasseldroid.protocol.testutil.quasselSerializerTest
import info.quasseldroid.protocol.types.BufferId
import info.quasseldroid.protocol.types.BufferInfo
import info.quasseldroid.protocol.types.BufferType
import info.quasseldroid.protocol.types.NetworkId
import info.quasseldroid.protocol.variant.QuasselType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BufferInfoSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      BufferInfoSerializer,
      QuasselSerializers.find<BufferInfo>(QuasselType.BufferInfo),
    )
  }

  @Test
  fun testBaseCase() = quasselSerializerTest(
    BufferInfoSerializer,
    BufferInfo(
      BufferId(-1),
      NetworkId(-1),
      BufferType.none(),
      -1,
      ""
    ),
    byteBufferOf(0xFFu, 0xFFu, 0xFFu, 0xFFu, 0xFFu, 0xFFu, 0xFFu, 0xFFu, 0x00u, 0x00u, 0xFFu, 0xFFu, 0xFFu, 0xFFu, 0x00u, 0x00u, 0x00u, 0x00u)
  )

  @Test
  fun testNormal() = quasselSerializerTest(
    BufferInfoSerializer,
    BufferInfo(
      BufferId.MAX_VALUE,
      NetworkId.MAX_VALUE,
      BufferType.validValues(),
      Int.MAX_VALUE,
      "äẞ\u0000\uFFFF"
    ),
    byteBufferOf(127, -1, -1, -1, 127, -1, -1, -1, 0, 15, 127, -1, -1, -1, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65)
  )
}
