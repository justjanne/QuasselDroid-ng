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
package de.justjanne.libquassel.protocol.serializers.primitive

import de.justjanne.libquassel.protocol.serializers.NoSerializerForTypeException
import de.justjanne.libquassel.protocol.serializers.QtSerializers
import de.justjanne.libquassel.protocol.testutil.byteBufferOf
import de.justjanne.libquassel.protocol.testutil.deserialize
import de.justjanne.libquassel.protocol.variant.QVariant_
import de.justjanne.libquassel.protocol.variant.QtType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class QVariantSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      QVariantSerializer,
      QtSerializers.find<QVariant_>(QtType.QVariant),
    )
  }

  @Test
  fun testUnregisteredQtType() {
    assertThrows<NoSerializerForTypeException> {
      deserialize(
        QVariantSerializer,
        byteBufferOf(0x00u, 0x00u, 0x01u, 0x00u, 0x00u)
      )
    }
  }

  @Test
  fun testUnknownQtType() {
    assertThrows<NoSerializerForTypeException> {
      deserialize(
        QVariantSerializer,
        byteBufferOf(0x00u, 0xFFu, 0x00u, 0x00u, 0x00u)
      )
    }
  }

  @Test
  fun testUnregisteredQuasselType() {
    assertThrows<NoSerializerForTypeException> {
      deserialize(
        QVariantSerializer,
        byteBufferOf(
          // QtType
          0x00u, 0x00u, 0x00u, 0x7Fu,
          // isNull
          0x00u,
          // QuasselType length
          0x00u, 0x00u, 0x00u, 0x00u,
        )
      )
    }
  }

  @Test
  fun testUnknownQuasselType() {
    assertThrows<NoSerializerForTypeException> {
      deserialize(
        QVariantSerializer,
        byteBufferOf(
          // QtType
          0x00u, 0x00u, 0x00u, 0x7Fu,
          // isNull
          0x00u,
          // QuasselType length
          0x00u, 0x00u, 0x00u, 0x03u,
          // "foo"
          0x66u, 0x6fu, 0x6fu
        )
      )
    }
  }
}
