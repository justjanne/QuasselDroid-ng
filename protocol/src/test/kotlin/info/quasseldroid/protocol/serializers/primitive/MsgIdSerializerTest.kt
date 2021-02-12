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
import info.quasseldroid.protocol.types.MsgId
import info.quasseldroid.protocol.variant.QuasselType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MsgIdSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      MsgIdSerializer,
      QuasselSerializers.find<MsgId>(QuasselType.MsgId),
    )
  }

  @Test
  fun testZero() = quasselSerializerTest(
    MsgIdSerializer,
    MsgId(0),
    byteBufferOf(0, 0, 0, 0, 0, 0, 0, 0),
    featureSets = listOf(FeatureSet.all())
  )

  @Test
  fun testMinimal() = quasselSerializerTest(
    MsgIdSerializer,
    MsgId.MIN_VALUE,
    byteBufferOf(-128, 0, 0, 0, 0, 0, 0, 0),
    featureSets = listOf(FeatureSet.all())
  )

  @Test
  fun testMaximal() = quasselSerializerTest(
    MsgIdSerializer,
    MsgId.MAX_VALUE,
    byteBufferOf(127, -1, -1, -1, -1, -1, -1, -1),
    featureSets = listOf(FeatureSet.all())
  )

  @Test
  fun testAllOnes() = quasselSerializerTest(
    MsgIdSerializer,
    MsgId(0.inv()),
    byteBufferOf(-1, -1, -1, -1, -1, -1, -1, -1),
    featureSets = listOf(FeatureSet.all())
  )
}
