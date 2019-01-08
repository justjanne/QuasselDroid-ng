/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.Buffer_Types
import de.kuschku.libquassel.protocol.primitive.serializer.VariantMapSerializer
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.libquassel.session.SignalProxy
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test

class AliasManagerTest {
  @Test
  fun testSerialization() {
    val original = AliasManager(SignalProxy.NULL)
    original.setAliasList(original.defaults())

    val copy = original.copy()
    copy.fromVariantMap(roundTrip(VariantMapSerializer, original.toVariantMap()))
    assert(original.isEqual(copy))
  }

  @Test
  fun testCopy() {
    val original = AliasManager(SignalProxy.NULL)
    original.setAliasList(original.defaults())

    val copy = original.copy()
    copy.fromVariantMap(original.toVariantMap())
    assert(original.isEqual(copy))
  }

  @Test
  fun testExpansion() {
    fun testExpansion(aliases: List<IAliasManager.Alias>, original: String,
                      expanded: List<String>) {
      val manager = AliasManager(SignalProxy.NULL)
      manager.setAliasList(manager.defaults() + aliases)

      val bufferInfo = BufferInfo(
        bufferId = -1,
        networkId = -1,
        type = Buffer_Types.of(Buffer_Type.StatusBuffer),
        bufferName = "#quassel-test",
        groupId = -1
      )

      val previousCommands = mutableListOf<IAliasManager.Command>()
      manager.processInput(
        info = bufferInfo,
        message = original,
        previousCommands = previousCommands
      )

      assertEquals(previousCommands, expanded.map {
        IAliasManager.Command(bufferInfo, it)
      })
    }

    testExpansion(
      listOf(
        IAliasManager.Alias(
          name = "d",
          expansion = "/say first \"\$1\" second \"\$2\" some \"\$3..4\" more \"\$3..\""
        )
      ),
      "/d a b c d e f",
      listOf(
        "/say first \"a\" second \"b\" some \"c d\" more \"c d e f\""
      )
    )

    testExpansion(
      listOf(
        IAliasManager.Alias(
          name = "test",
          expansion = "Test $1; Test $2; Test All $0"
        )
      ),
      "/test 1 2 3",
      listOf(
        "Test 1",
        "Test 2",
        "Test All 1 2 3"
      )
    )
  }
}
