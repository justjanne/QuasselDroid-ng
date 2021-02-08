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
package de.kuschku.libquassel.protocol.serializers.primitive

import de.kuschku.libquassel.protocol.serializers.QtSerializers
import de.kuschku.libquassel.protocol.testutil.byteBufferOf
import de.kuschku.libquassel.protocol.testutil.matchers.MapMatcher
import de.kuschku.libquassel.protocol.testutil.qtSerializerTest
import de.kuschku.libquassel.protocol.variant.QVariantMap
import de.kuschku.libquassel.protocol.variant.QtType
import de.kuschku.libquassel.protocol.variant.qVariant
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QVariantMapSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      QVariantMapSerializer,
      QtSerializers.find<QVariantMap>(QtType.QVariantMap),
    )
  }

  @Test
  fun testEmpty() = qtSerializerTest(
    QVariantMapSerializer,
    mapOf(),
    byteBufferOf(0, 0, 0, 0)
  )

  @Test
  fun testNormal() = qtSerializerTest(
    QVariantMapSerializer,
    mapOf(
      "Username" to qVariant("AzureDiamond", QtType.QString),
      "Password" to qVariant("hunter2", QtType.QString)
    ),
    byteBufferOf(0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x10, 0x00, 0x55, 0x00, 0x73, 0x00, 0x65, 0x00, 0x72, 0x00, 0x6E, 0x00, 0x61, 0x00, 0x6D, 0x00, 0x65, 0x00, 0x00, 0x00, 0x0A, 0x00, 0x00, 0x00, 0x00, 0x18, 0x00, 0x41, 0x00, 0x7A, 0x00, 0x75, 0x00, 0x72, 0x00, 0x65, 0x00, 0x44, 0x00, 0x69, 0x00, 0x61, 0x00, 0x6D, 0x00, 0x6F, 0x00, 0x6E, 0x00, 0x64, 0x00, 0x00, 0x00, 0x10, 0x00, 0x50, 0x00, 0x61, 0x00, 0x73, 0x00, 0x73, 0x00, 0x77, 0x00, 0x6F, 0x00, 0x72, 0x00, 0x64, 0x00, 0x00, 0x00, 0x0A, 0x00, 0x00, 0x00, 0x00, 0x0E, 0x00, 0x68, 0x00, 0x75, 0x00, 0x6E, 0x00, 0x74, 0x00, 0x65, 0x00, 0x72, 0x00, 0x32),
    ::MapMatcher
  )

  @Test
  fun testNullKey() = qtSerializerTest(
    QVariantMapSerializer,
    mapOf(
      "" to qVariant<String?>(null, QtType.QString)
    ),
    byteBufferOf(
      // length
      0x00u, 0x00u, 0x00u, 0x01u,
      // length of key
      0xFFu, 0xFFu, 0xFFu, 0xFFu,
      // type of value
      0x00u, 0x00u, 0x00u, 0x0Au,
      // isNull of value
      0x00u,
      // length of value
      0xFFu, 0xFFu, 0xFFu, 0xFFu
    ),
    ::MapMatcher,
    serializeFeatureSet = null
  )
}

