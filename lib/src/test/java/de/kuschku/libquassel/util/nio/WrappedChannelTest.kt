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

package de.kuschku.libquassel.util.nio

import de.kuschku.libquassel.util.nio.WrappedChannel.Companion.selectBestTlsVersion
import org.junit.Assert.assertEquals
import org.junit.Test

class WrappedChannelTest {
  @Test
  fun doesNotSelectOutdatedTlsVersions() {
    assertEquals(null, selectBestTlsVersion(arrayOf(
      "SSLv3", "TLSv1", "TLSv1.0", "TLSv1.1"
    )))
  }

  @Test
  fun rejectsNonTlsProtocols() {
    assertEquals(null, selectBestTlsVersion(arrayOf(
      "SSLv3", "UberSecurityProtocol5"
    )))
  }

  @Test
  fun selectsLatestTlsVersion() {
    assertEquals("TLSv1.2", selectBestTlsVersion(arrayOf(
      "SSLv3", "TLSv1", "TLSv1.0", "TLSv1.1", "TLSv1.2", "UberSecurityProtocol5"
    )))
    assertEquals("TLSv1.3", selectBestTlsVersion(arrayOf(
      "SSLv3", "TLSv1", "TLSv1.0", "TLSv1.1", "TLSv1.2", "TLSv1.3", "UberSecurityProtocol5"
    )))
  }
}
