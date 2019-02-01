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

import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.util.withTestSession
import org.junit.Test

/*
 * When implementing SignedIds properly for the first time, we noticed that they were used in
 * objectNames directly, leading e.g. IrcChannel’s renameObject("${network().networkId()}/${name()}")
 * to return "NetworkId(4)/justJanne" instead of the "4/justJanne" that it used to.
 *
 * This test exists to prevent this regression from reoccuring.
 */
class SignedIdNameTests {
  @Test
  fun testNetworkName() = withTestSession {
    ensure {
      addNetwork(buildNetwork(NetworkId(2)), initialize = true)
    }.does {
      requestInit("Network", "2")
    }
  }

  @Test
  fun testIdentityName() = withTestSession {
    ensure {
      addIdentity(buildIdentity(IdentityId(2)), initialize = true)
    }.does {
      requestInit("Identity", "2")
    }
  }

  @Test
  fun testIrcUserName() = withTestSession {
    ensure {
      val network = network(NetworkId(1))!!
      network.addIrcUser("testuser")
    }.does {
      requestInit("IrcUser", "1/testuser")
    }
  }

  @Test
  fun testIrcChannelName() = withTestSession {
    ensure {
      val network = network(NetworkId(1))!!
      network.addIrcChannel("#testchannel")
    }.does {
      requestInit("IrcChannel", "1/#testchannel")
    }
  }
}
