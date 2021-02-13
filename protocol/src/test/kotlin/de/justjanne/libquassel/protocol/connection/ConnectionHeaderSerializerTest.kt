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

package de.justjanne.libquassel.protocol.connection

import de.justjanne.bitflags.of
import de.justjanne.libquassel.protocol.testutil.byteBufferOf
import de.justjanne.libquassel.protocol.testutil.serializerTest
import org.junit.jupiter.api.Test

class ConnectionHeaderSerializerTest {
  @Test
  fun testQuasseldroid() = serializerTest(
    ConnectionHeaderSerializer,
    ConnectionHeader(
      features = ProtocolFeature.of(
        ProtocolFeature.TLS,
        ProtocolFeature.Compression,
      ),
      versions = listOf(
        ProtocolMeta(
          data = 0x0000u,
          version = ProtocolVersion.Datastream,
        )
      )
    ),
    byteBufferOf(
      0x42u, 0xb3u, 0x3fu, 0x03u,
      0x80u, 0x00u, 0x00u, 0x02u
    )
  )

  @Test
  fun testQuasselClient() = serializerTest(
    ConnectionHeaderSerializer,
    ConnectionHeader(
      features = ProtocolFeature.of(
        ProtocolFeature.TLS,
        ProtocolFeature.Compression,
      ),
      versions = listOf(
        ProtocolMeta(
          data = 0x0000u,
          version = ProtocolVersion.Legacy,
        ),
        ProtocolMeta(
          data = 0x0000u,
          version = ProtocolVersion.Datastream,
        )
      )
    ),
    byteBufferOf(
      0x42u, 0xb3u, 0x3fu, 0x03u,
      0x00u, 0x00u, 0x00u, 0x01u,
      0x80u, 0x00u, 0x00u, 0x02u
    )
  )

  @Test
  fun testDebugClient() = serializerTest(
    ConnectionHeaderSerializer,
    ConnectionHeader(
      features = ProtocolFeature.of(),
      versions = listOf(
        ProtocolMeta(
          data = 0x0000u,
          version = ProtocolVersion.Legacy,
        ),
        ProtocolMeta(
          data = 0x0000u,
          version = ProtocolVersion.Datastream,
        )
      )
    ),
    byteBufferOf(
      0x42u, 0xb3u, 0x3fu, 0x00u,
      0x00u, 0x00u, 0x00u, 0x01u,
      0x80u, 0x00u, 0x00u, 0x02u
    )
  )
}
