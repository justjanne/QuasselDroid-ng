/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
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

import de.kuschku.libquassel.protocol.Buffer_Activity
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.*
import org.junit.Test

class BufferViewConfigTest {
  @Test
  fun testSerialization() {
    val original = BufferViewConfig(randomUInt(), SignalProxy.NULL)
    original.setBufferViewName(randomString())
    original.setNetworkId(randomUInt())
    original.setAddNewBuffersAutomatically(randomBoolean())
    original.setSortAlphabetically(randomBoolean())
    original.setHideInactiveNetworks(randomBoolean())
    original.setHideInactiveNetworks(randomBoolean())
    original.setDisableDecoration(randomBoolean())
    original.setAllowedBufferTypes(Buffer_Type.of(*Buffer_Type.validValues))
    original.setMinimumActivity(randomOf(*Buffer_Activity.values()).toInt())
    original.setShowSearch(randomBoolean())

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    assert(original.isEqual(copy))
  }

  @Test
  fun testCopy() {
    val original = BufferViewConfig(randomUInt(), SignalProxy.NULL)
    original.setBufferViewName(randomString())
    original.setNetworkId(randomUInt())
    original.setAddNewBuffersAutomatically(randomBoolean())
    original.setSortAlphabetically(randomBoolean())
    original.setHideInactiveNetworks(randomBoolean())
    original.setHideInactiveNetworks(randomBoolean())
    original.setDisableDecoration(randomBoolean())
    original.setAllowedBufferTypes(Buffer_Type.of(*Buffer_Type.validValues))
    original.setMinimumActivity(randomOf(*Buffer_Activity.values()).toInt())
    original.setShowSearch(randomBoolean())

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    assert(original.isEqual(copy))
  }
}
