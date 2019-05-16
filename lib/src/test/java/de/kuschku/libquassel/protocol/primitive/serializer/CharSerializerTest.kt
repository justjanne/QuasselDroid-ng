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

class CharSerializerTest {
  @Test
  fun testAll() {
    assertEquals(' ', roundTrip(CharSerializer, ' '))
    // @formatter:off
    assertEquals(' ', deserialize(CharSerializer, byteArrayOf(0, 32)))
    // @formatter:on

    assertEquals('a', roundTrip(CharSerializer, 'a'))
    // @formatter:off
    assertEquals('a', deserialize(CharSerializer, byteArrayOf(0, 97)))
    // @formatter:on

    assertEquals('ä', roundTrip(CharSerializer, 'ä'))
    // @formatter:off
    assertEquals('ä', deserialize(CharSerializer, byteArrayOf(0, -28)))
    // @formatter:on

    assertEquals('\u0000', roundTrip(CharSerializer, '\u0000'))
    // @formatter:off
    assertEquals('\u0000', deserialize(CharSerializer, byteArrayOf(0, 0)))
    // @formatter:on

    assertEquals('\uFFFF', roundTrip(CharSerializer, '\uFFFF'))
    // @formatter:off
    assertEquals('\uFFFF', deserialize(CharSerializer, byteArrayOf(-1, -1)))
    // @formatter:on
  }
}
