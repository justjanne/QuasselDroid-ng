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

package de.kuschku.libquassel.integration

import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QuasselType
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.QtType
import de.kuschku.libquassel.util.withTestSession
import org.junit.Test

class SampleIntegrationTest {
  @Test
  fun test() = withTestSession {
    ensure {
      rpcHandler.changePassword(0UL, "user", "pass", "p@ssword1")
    }.does {
      callRpc(
        "2changePassword(PeerPtr,QString,QString,QString)",
        listOf(
          QVariant_.of(0UL, QuasselType.PeerPtr),
          QVariant_.of("user", QtType.QString),
          QVariant_.of("pass", QtType.QString),
          QVariant_.of("p@ssword1", QtType.QString)
        )
      )
    }

    ensure {
      val network = network(NetworkId(1))!!
      val justJanne = network.ircUser("justJanne")!!
      assert(justJanne.channels().contains("#quassel-test"))
    }
  }
}
