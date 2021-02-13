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

import de.justjanne.libquassel.protocol.serializers.QuasselSerializers
import de.justjanne.libquassel.protocol.testutil.byteBufferOf
import de.justjanne.libquassel.protocol.testutil.quasselSerializerTest
import de.justjanne.libquassel.protocol.types.DccIpDetectionMode
import de.justjanne.libquassel.protocol.variant.QuasselType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DccIpDetectionModeSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      DccIpDetectionModeSerializer,
      QuasselSerializers.find<DccIpDetectionMode>(
        QuasselType.DccConfigIpDetectionMode
      ),
    )
  }

  @Test
  fun testAutomatic() = quasselSerializerTest(
    DccIpDetectionModeSerializer,
    DccIpDetectionMode.Automatic,
    byteBufferOf(0x00u)
  )

  @Test
  fun testManual() = quasselSerializerTest(
    DccIpDetectionModeSerializer,
    DccIpDetectionMode.Manual,
    byteBufferOf(0x01u)
  )

  @Test
  fun testNull() = quasselSerializerTest(
    DccIpDetectionModeSerializer,
    null,
    byteBufferOf(0x00u),
    deserializeFeatureSet = null,
    featureSets = emptyList(),
  )
}
