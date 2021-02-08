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
import de.kuschku.libquassel.protocol.variant.QVariantList
import de.kuschku.libquassel.protocol.variant.QVariantMap
import de.kuschku.libquassel.protocol.variant.QtType
import de.kuschku.libquassel.protocol.variant.qVariant
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QVariantListSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      QVariantListSerializer,
      QtSerializers.find<QVariantList>(QtType.QVariantList),
    )
  }

  @Test
  fun testEmpty() = qtSerializerTest(
    QVariantListSerializer,
    listOf(),
    byteBufferOf(0, 0, 0, 0)
  )

  @Test
  fun testNormal() = qtSerializerTest(
    QVariantListSerializer,
    listOf(
      qVariant("AzureDiamond", QtType.QString),
      qVariant("hunter2", QtType.QString)
    ),
    byteBufferOf( 0x00u, 0x00u, 0x00u, 0x02u, 0x00u, 0x00u, 0x00u, 0x0Au, 0x00u, 0x00u, 0x00u, 0x00u, 0x18u, 0x00u, 0x41u, 0x00u, 0x7Au, 0x00u, 0x75u, 0x00u, 0x72u, 0x00u, 0x65u, 0x00u, 0x44u, 0x00u, 0x69u, 0x00u, 0x61u, 0x00u, 0x6Du, 0x00u, 0x6Fu, 0x00u, 0x6Eu, 0x00u, 0x64u, 0x00u, 0x00u, 0x00u, 0x0Au, 0x00u, 0x00u, 0x00u, 0x00u, 0x0Eu, 0x00u, 0x68u, 0x00u, 0x75u, 0x00u, 0x6Eu, 0x00u, 0x74u, 0x00u, 0x65u, 0x00u, 0x72u, 0x00u, 0x32u)
  )
}

