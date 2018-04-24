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

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.randomString
import de.kuschku.libquassel.util.randomUInt
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert
import org.junit.Test

class IdentityTest {
  @Test
  fun testSerialization() {
    val original = Identity(SignalProxy.NULL)
    original.setId(randomUInt())
    original.setIdentityName(randomString())
    original.setRealName(randomString())
    original.setNicks(listOf(
      randomString(),
      randomString(),
      randomString()
    ))

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    Assert.assertEquals(original, copy)
  }

  @Test
  fun testCopy() {
    val original = Identity(SignalProxy.NULL)
    original.setId(randomUInt())
    original.setIdentityName(randomString())
    original.setRealName(randomString())
    original.setNicks(listOf(
      randomString(),
      randomString(),
      randomString()
    ))

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    Assert.assertEquals(original, copy)
  }
}
