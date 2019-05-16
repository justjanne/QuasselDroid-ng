/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QType
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.util.withTestSession
import org.junit.Test

class SampleIntegrationTest {
  @Test
  fun test() = withTestSession {
    ensure {
      rpcHandler.changePassword(0L, "user", "pass", "p@ssword1")
    }.does {
      callRpc(
        "2changePassword(PeerPtr,QString,QString,QString)",
        listOf(
          QVariant_.of(0L, QType.PeerPtr),
          QVariant_.of("user", Type.QString),
          QVariant_.of("pass", Type.QString),
          QVariant_.of("p@ssword1", Type.QString)
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
