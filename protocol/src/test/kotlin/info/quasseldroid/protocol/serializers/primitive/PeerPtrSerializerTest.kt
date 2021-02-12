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
package info.quasseldroid.protocol.serializers.primitive

import info.quasseldroid.protocol.features.FeatureSet
import info.quasseldroid.protocol.serializers.QuasselSerializers
import info.quasseldroid.protocol.testutil.byteBufferOf
import info.quasseldroid.protocol.testutil.quasselSerializerTest
import info.quasseldroid.protocol.variant.QuasselType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PeerPtrSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      PeerPtrSerializer,
      QuasselSerializers.find<ULong>(QuasselType.PeerPtr),
    )
  }

  @Test
  fun testZero() = quasselSerializerTest(
    PeerPtrSerializer,
    0uL,
    byteBufferOf(0, 0, 0, 0, 0, 0, 0, 0),
    featureSets = listOf(FeatureSet.all())
  )

  @Test
  fun testMinimal() = quasselSerializerTest(
    PeerPtrSerializer,
    ULong.MIN_VALUE,
    byteBufferOf(0, 0, 0, 0, 0, 0, 0, 0),
    featureSets = listOf(FeatureSet.all())
  )

  @Test
  fun testMaximal() = quasselSerializerTest(
    PeerPtrSerializer,
    ULong.MAX_VALUE,
    byteBufferOf(-1, -1, -1, -1, -1, -1, -1, -1),
    featureSets = listOf(FeatureSet.all())
  )

  @Test
  fun testAllOnes() = quasselSerializerTest(
    PeerPtrSerializer,
    0uL.inv(),
    byteBufferOf(-1, -1, -1, -1, -1, -1, -1, -1),
    featureSets = listOf(FeatureSet.all())
  )
}
