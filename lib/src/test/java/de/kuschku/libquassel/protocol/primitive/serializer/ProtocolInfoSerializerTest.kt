/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

import de.kuschku.libquassel.protocol.Protocol
import de.kuschku.libquassel.protocol.Protocol_Feature
import de.kuschku.libquassel.quassel.ProtocolInfo
import de.kuschku.libquassel.util.deserialize
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test

class ProtocolInfoSerializerTest {
  @Test
  fun testNone() {
    val data = ProtocolInfo(
      flags = Protocol_Feature.of(),
      data = 0u,
      version = 0u
    )
    assertEquals(data, roundTrip(ProtocolInfoSerializer, data))
    // @formatter:off
    assertEquals(data, deserialize(ProtocolInfoSerializer, byteArrayOf(0, 0, 0, 0)))
    // @formatter:on
  }

  @Test
  fun testUsual() {
    val data = ProtocolInfo(
      flags = Protocol_Feature.of(
        Protocol_Feature.TLS,
        Protocol_Feature.Compression
      ),
      data = 0u,
      version = Protocol.Datastream.toUByte()
    )
    assertEquals(data, roundTrip(ProtocolInfoSerializer, data))
    // @formatter:off
    assertEquals(data, deserialize(ProtocolInfoSerializer, byteArrayOf(3, 0, 0, 2)))
    // @formatter:on
  }

  @Test
  fun testExtreme() {
    val data = ProtocolInfo(
      flags = Protocol_Feature.of(
        Protocol_Feature.TLS,
        Protocol_Feature.Compression
      ),
      data = 0xffffu,
      version = 0xffu
    )
    assertEquals(data, roundTrip(ProtocolInfoSerializer, data))
    // @formatter:off
    assertEquals(data, deserialize(ProtocolInfoSerializer, byteArrayOf(3, -1, -1, -1)))
    // @formatter:on
  }
}
