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

class BoolSerializerTest {
  @Test
  fun test() {
    assertEquals(true, roundTrip(BoolSerializer, true))
    // @formatter:off
    assertEquals(true, deserialize(BoolSerializer, byteArrayOf(1)))
    // @formatter:on

    assertEquals(false, roundTrip(BoolSerializer, false))
    // @formatter:off
    assertEquals(false, deserialize(BoolSerializer, byteArrayOf(0)))
    // @formatter:on
  }
}
