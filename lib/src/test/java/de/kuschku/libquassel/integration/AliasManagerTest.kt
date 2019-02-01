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

package de.kuschku.libquassel.integration

import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.libquassel.util.TestSession
import de.kuschku.libquassel.util.setupTestSession
import de.kuschku.libquassel.util.with
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AliasManagerTest {
  lateinit var session: TestSession
  lateinit var channelBuffer: BufferInfo
  lateinit var queryBuffer: BufferInfo

  @Before
  fun setUp() {
    session = setupTestSession()
    session.aliasManager.setAliasList(listOf(
      IAliasManager.Alias(
        "userexpansion",
        "$1 $1:account $1:hostname $1:identd $1:ident"
      ),
      IAliasManager.Alias(
        "channelexpansion",
        "\$channel"
      ),
      IAliasManager.Alias(
        "rangeexpansion",
        "1 \"\$1\" 2 \"\$2\" 3..4 \"\$3..4\" 3.. \"\$3..\""
      )
    ))

    channelBuffer = session.bufferSyncer.find(
      bufferName = "#quassel-test",
      networkId = NetworkId(1)
    )!!
    assert(channelBuffer.type == Buffer_Type.of(Buffer_Type.ChannelBuffer))

    queryBuffer = session.bufferSyncer.find(
      bufferName = "digitalcircuit",
      networkId = NetworkId(1)
    )!!
    assert(queryBuffer.type == Buffer_Type.of(Buffer_Type.QueryBuffer))
  }

  // Test with user where identd works
  @Test
  fun userExpansionWithIdentd() = session.with {
    assertEquals(
      listOf(IAliasManager.Command(
        channelBuffer,
        "justJanne justJanne kuschku.de kuschku kuschku"
      )),
      aliasManager.processInput(
        channelBuffer,
        "/userexpansion justJanne"
      )
    )
  }

  // Test with user where identd doesn’t work
  @Test
  fun userExpansionNoIdentd() = session.with {
    assertEquals(
      listOf(IAliasManager.Command(
        channelBuffer,
        "digitalcircuit digitalcircuit 2605:6000:1518:830d:ec4:7aff:fe6b:c6b0 * ~quassel"
      )),
      aliasManager.processInput(
        channelBuffer,
        "/userexpansion digitalcircuit"
      )
    )
  }

  // Test with user that isn’t even in channel
  @Test
  fun userExpansionUnknownUser() = session.with {
    assertEquals(
      listOf(IAliasManager.Command(
        channelBuffer,
        "ChanServ * * * *"
      )),
      aliasManager.processInput(
        channelBuffer,
        "/userexpansion ChanServ"
      )
    )
  }

  // Test in query
  @Test
  fun userExpansionQuery() = session.with {
    assertEquals(
      listOf(IAliasManager.Command(
        queryBuffer,
        "digitalcircuit digitalcircuit 2605:6000:1518:830d:ec4:7aff:fe6b:c6b0 * ~quassel"
      )),
      aliasManager.processInput(
        queryBuffer,
        "/userexpansion digitalcircuit"
      )
    )
  }

  @Test
  fun channelExpansionChannel() = session.with {
    assertEquals(
      listOf(IAliasManager.Command(
        channelBuffer,
        "#quassel-test"
      )),
      aliasManager.processInput(
        channelBuffer,
        "/channelexpansion"
      )
    )
  }

  @Test
  fun channelExpansionQuery() = session.with {
    assertEquals(
      listOf(IAliasManager.Command(
        queryBuffer,
        "digitalcircuit"
      )),
      aliasManager.processInput(
        queryBuffer,
        "/channelexpansion"
      )
    )
  }

  @Test
  fun rangeExpansion() = session.with {
    assertEquals(
      listOf(IAliasManager.Command(
        queryBuffer,
        "1 \"a\" 2 \"b\" 3..4 \"c d\" 3.. \"c d e f\""
      )),
      aliasManager.processInput(
        queryBuffer,
        "/rangeexpansion a b c d e f"
      )
    )
  }
}
