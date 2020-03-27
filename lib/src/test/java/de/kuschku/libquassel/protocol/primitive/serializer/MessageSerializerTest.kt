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

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.deserialize
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.Instant

class MessageSerializerTest {
  @Test
  fun testBaseCaseNoFeatures() {
    val value = Message(
      MsgId(-1),
      Instant.EPOCH,
      Message_Type.of(),
      Message_Flag.of(),
      BufferInfo(
        BufferId(-1),
        NetworkId(-1),
        Buffer_Type.of(),
        -1,
        ""
      ),
      "",
      "",
      "",
      "",
      ""
    )
    val other = roundTrip(MessageSerializer, value, features = QuasselFeatures.empty())
    assertEquals(value, other)
    // @formatter:off
    assertEquals(value, deserialize(MessageSerializer, byteArrayOf(-1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), features = QuasselFeatures.empty()))
    // @formatter:on
  }

  @Test
  fun testNormalNoFeatures() {
    val value = Message(
      MsgId(Int.MAX_VALUE.toLong()),
      Instant.ofEpochMilli(1524601750000),
      Message_Type.of(*Message_Type.values()),
      Message_Flag.of(*Message_Flag.values()),
      BufferInfo(
        BufferId.MAX_VALUE,
        NetworkId.MAX_VALUE,
        Buffer_Type.of(*Buffer_Type.validValues),
        Int.MAX_VALUE,
        "äẞ\u0000\uFFFF"
      ),
      "äẞ\u0000\uFFFF",
      "",
      "",
      "",
      "äẞ\u0000\uFFFF"
    )
    val other = roundTrip(MessageSerializer, value, features = QuasselFeatures.empty())
    assertEquals(value, other)
    // @formatter:off
    assertEquals(value, deserialize(MessageSerializer, byteArrayOf(127, -1, -1, -1, 90, -33, -109, -106, 0, 7, -1, -1, -113, 127, -1, -1, -1, 127, -1, -1, -1, 0, 15, 127, -1, -1, -1, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65), features = QuasselFeatures.empty()))
    // @formatter:on
  }

  @Test
  fun testBaseCaseAllFeatures() {
    val value = Message(
      MsgId(-1),
      Instant.EPOCH,
      Message_Type.of(),
      Message_Flag.of(),
      BufferInfo(
        BufferId(-1),
        NetworkId(-1),
        Buffer_Type.of(),
        -1,
        ""
      ),
      "",
      "",
      "",
      "",
      ""
    )
    val other = roundTrip(MessageSerializer, value, features = QuasselFeatures.all())
    assertEquals(value, other)
    // @formatter:off
    assertEquals(value, deserialize(MessageSerializer, byteArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), features = QuasselFeatures.all()))
    // @formatter:on
  }

  @Test
  fun testNormalAllFeatures() {
    val value = Message(
      MsgId.MAX_VALUE,
      Instant.ofEpochMilli(1524601750000),
      Message_Type.of(*Message_Type.values()),
      Message_Flag.of(*Message_Flag.values()),
      BufferInfo(
        BufferId.MAX_VALUE,
        NetworkId.MAX_VALUE,
        Buffer_Type.of(*Buffer_Type.validValues),
        Int.MAX_VALUE,
        "äẞ\u0000\uFFFF"
      ),
      "äẞ\u0000\uFFFF",
      "äẞ\u0000\uFFFF",
      "äẞ\u0000\uFFFF",
      "äẞ\u0000\uFFFF",
      "äẞ\u0000\uFFFF"
    )
    val other = roundTrip(MessageSerializer, value, features = QuasselFeatures.all())
    assertEquals(value, other)
    // @formatter:off
    assertEquals(value, deserialize(MessageSerializer, byteArrayOf(127, -1, -1, -1, -1, -1, -1, -1, 0, 0, 1, 98, -7, 88, -127, -16, 0, 7, -1, -1, -113, 127, -1, -1, -1, 127, -1, -1, -1, 0, 15, 127, -1, -1, -1, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65), features = QuasselFeatures.all()))
    // @formatter:on
  }

  @Test
  fun testExtremeAllFeatures() {
    val value = Message(
      MsgId.MAX_VALUE,
      Instant.ofEpochMilli(Int.MAX_VALUE * 10000L),
      Message_Type.of(*Message_Type.values()),
      Message_Flag.of(*Message_Flag.values()),
      BufferInfo(
        BufferId.MAX_VALUE,
        NetworkId.MAX_VALUE,
        Buffer_Type.of(*Buffer_Type.validValues),
        Int.MAX_VALUE,
        "äẞ\u0000\uFFFF"
      ),
      "äẞ\u0000\uFFFF",
      "äẞ\u0000\uFFFF",
      "äẞ\u0000\uFFFF",
      "äẞ\u0000\uFFFF",
      "äẞ\u0000\uFFFF"
    )
    val other = roundTrip(MessageSerializer, value, features = QuasselFeatures.all())
    assertEquals(value, other)
    // @formatter:off
    assertEquals(value, deserialize(MessageSerializer, byteArrayOf(127, -1, -1, -1, -1, -1, -1, -1, 0, 0, 19, -121, -1, -1, -40, -16, 0, 7, -1, -1, -113, 127, -1, -1, -1, 127, -1, -1, -1, 0, 15, 127, -1, -1, -1, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65, 0, 0, 0, 9, -61, -92, -31, -70, -98, 0, -17, -65, -65), features = QuasselFeatures.all()))
    // @formatter:on
  }
}
