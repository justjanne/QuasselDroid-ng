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

package de.kuschku.quasseldroid.ssl.custom

import de.kuschku.libquassel.ssl.toJavaCertificate
import de.kuschku.quasseldroid.persistence.dao.SslHostnameWhitelistDao
import de.kuschku.quasseldroid.util.helper.sha1Fingerprint
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

class QuasselHostnameManager(
  private val hostnameWhitelist: SslHostnameWhitelistDao
) : HostnameVerifier {
  override fun verify(hostname: String?, session: SSLSession?): Boolean {
    val chain = session?.peerCertificateChain?.toJavaCertificate()
    val leafCertificate = chain?.firstOrNull() ?: return false
    val whitelistEntry = hostnameWhitelist.find(leafCertificate.sha1Fingerprint, hostname ?: "")
    return whitelistEntry != null
  }
}
