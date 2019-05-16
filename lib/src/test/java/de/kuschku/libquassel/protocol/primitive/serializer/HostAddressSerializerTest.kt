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

import de.kuschku.libquassel.util.deserialize
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.InetAddress

class HostAddressSerializerTest {
  @Test
  fun testIpv4() {
    val address = InetAddress.getByName("176.9.136.3")
    assertEquals(address, roundTrip(HostAddressSerializer, address))
    // @formatter:off
    assertEquals(address, deserialize(HostAddressSerializer, byteArrayOf(0, -80, 9, -120, 3)))
    // @formatter:on
  }

  @Test
  fun testIpv6() {
    val address = InetAddress.getByName("[2a01:4f8:160:1012::2]")
    assertEquals(address, roundTrip(HostAddressSerializer, address))
    // @formatter:off
    assertEquals(address, deserialize(HostAddressSerializer, byteArrayOf(1, 42, 1, 4, -8, 1, 96, 16, 18, 0, 0, 0, 0, 0, 0, 0, 2)))
    // @formatter:on
  }
}
