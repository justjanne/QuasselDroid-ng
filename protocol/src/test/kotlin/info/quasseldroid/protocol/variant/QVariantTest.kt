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

package info.quasseldroid.protocol.variant

import info.quasseldroid.protocol.testutil.byteBufferOf
import info.quasseldroid.protocol.types.BufferId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class QVariantTest {
  @Test
  fun testString() {
    assertEquals(
      "QVariant(ByteBufferSerializer, DEADBEEF)",
      qVariant(
        byteBufferOf(0xDEu, 0xADu, 0xBEu, 0xEFu),
        QtType.QByteArray
      ).toString()
    )
    assertEquals(
      "QVariant(StringSerializerUtf16, DEADBEEF)",
      qVariant(
        "DEADBEEF",
        QtType.QString
      ).toString()
    )
    assertEquals(
      "QVariant(BufferIdSerializer, BufferId(-1))",
      qVariant(
        BufferId(-1),
        QuasselType.BufferId
      ).toString()
    )
  }
}
