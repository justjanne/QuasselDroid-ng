/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
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
import de.kuschku.libquassel.protocol.Message
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.Instant

class MessageSerializerTest {
  @Test
  fun testBaseCaseNoFeatures() {
    val value = Message(
      -1,
      Instant.EPOCH,
      Message_Type.of(),
      Message_Flag.of(),
      BufferInfo(
        -1,
        -1,
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
  }

  @Test
  fun testNormalNoFeatures() {
    val value = Message(
      Int.MAX_VALUE,
      Instant.ofEpochMilli(1524601750000),
      Message_Type.of(*Message_Type.values()),
      Message_Flag.of(*Message_Flag.values()),
      BufferInfo(
        Int.MAX_VALUE,
        Int.MAX_VALUE,
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
  }

  @Test
  fun testBaseCaseAllFeatures() {
    val value = Message(
      -1,
      Instant.EPOCH,
      Message_Type.of(),
      Message_Flag.of(),
      BufferInfo(
        -1,
        -1,
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
  }

  @Test
  fun testNormalAllFeatures() {
    val value = Message(
      Int.MAX_VALUE,
      Instant.ofEpochMilli(1524601750000),
      Message_Type.of(*Message_Type.values()),
      Message_Flag.of(*Message_Flag.values()),
      BufferInfo(
        Int.MAX_VALUE,
        Int.MAX_VALUE,
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
  }

  @Test
  fun testExtremeAllFeatures() {
    val value = Message(
      Int.MAX_VALUE,
      Instant.ofEpochMilli(Int.MAX_VALUE * 10000L),
      Message_Type.of(*Message_Type.values()),
      Message_Flag.of(*Message_Flag.values()),
      BufferInfo(
        Int.MAX_VALUE,
        Int.MAX_VALUE,
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
  }
}
