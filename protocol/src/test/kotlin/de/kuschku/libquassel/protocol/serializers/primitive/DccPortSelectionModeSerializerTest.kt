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

import de.kuschku.libquassel.protocol.serializers.QuasselSerializers
import de.kuschku.libquassel.protocol.testutil.byteBufferOf
import de.kuschku.libquassel.protocol.testutil.quasselSerializerTest
import de.kuschku.libquassel.protocol.types.DccIpDetectionMode
import de.kuschku.libquassel.protocol.types.DccPortSelectionMode
import de.kuschku.libquassel.protocol.variant.QuasselType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DccPortSelectionModeSerializerTest {
  @Test
  fun testIsRegistered() {
    assertEquals(
      DccPortSelectionModeSerializer,
      QuasselSerializers.find<DccPortSelectionMode>(
        QuasselType.DccConfigPortSelectionMode
      ),
    )
  }

  @Test
  fun testAutomatic() = quasselSerializerTest(
    DccPortSelectionModeSerializer,
    DccPortSelectionMode.Automatic,
    byteBufferOf(0x00u)
  )

  @Test
  fun testManual() = quasselSerializerTest(
    DccPortSelectionModeSerializer,
    DccPortSelectionMode.Manual,
    byteBufferOf(0x01u)
  )
}
