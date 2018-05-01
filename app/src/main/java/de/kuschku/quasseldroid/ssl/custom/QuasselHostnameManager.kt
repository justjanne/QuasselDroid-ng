/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
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

package de.kuschku.quasseldroid.ssl.custom

import de.kuschku.libquassel.connection.SocketAddress
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.util.helper.fingerprint
import java.security.cert.X509Certificate

class QuasselHostnameManager(
  private val hostnameWhitelist: QuasselDatabase.SslHostnameWhitelistDao
) {
  fun isValid(address: SocketAddress, chain: Array<out X509Certificate>): Boolean {
    val leafCertificate = chain.firstOrNull() ?: return false
    val whitelistEntry = hostnameWhitelist.find(leafCertificate.fingerprint, address.host)
    return whitelistEntry != null
  }
}
