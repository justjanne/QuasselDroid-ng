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

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.randomInt
import de.kuschku.libquassel.util.randomString
import de.kuschku.libquassel.util.roundTrip
import org.junit.jupiter.api.Test

class IdentityTest {
  @Test
  fun testSerialization() {
    val original = Identity(SignalProxy.NULL)
    original.setId(IdentityId(randomInt()))
    original.setIdentityName(randomString())
    original.setRealName(randomString())
    original.setNicks(listOf(
      randomString(),
      randomString(),
      randomString()
    ))

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    assert(original.isEqual(copy)) {
      System.err.println("Original:\n$original")
      System.err.println("Copy:\n$copy")
    }
  }

  @Test
  fun testCopy() {
    val original = Identity(SignalProxy.NULL)
    original.setId(IdentityId(randomInt()))
    original.setIdentityName(randomString())
    original.setRealName(randomString())
    original.setNicks(listOf(
      randomString(),
      randomString(),
      randomString()
    ))

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    assert(original.isEqual(copy)) {
      System.err.println("Original:\n$original")
      System.err.println("Copy:\n$copy")
    }
  }
}
