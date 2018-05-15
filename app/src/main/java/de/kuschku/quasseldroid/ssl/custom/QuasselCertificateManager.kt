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

import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.util.helper.fingerprint
import de.kuschku.quasseldroid.util.helper.isValid
import java.security.cert.X509Certificate

class QuasselCertificateManager(
  private val validityWhitelist: QuasselDatabase.SslValidityWhitelistDao
) {
  fun isServerTrusted(chain: Array<out X509Certificate>?): Boolean {
    // Verify input conditions
    // If no certificate exists, this can’t be valid
    val leafCertificate = chain?.firstOrNull() ?: return false
    return isServerTrusted(leafCertificate)
  }

  private fun isServerTrusted(leafCertificate: X509Certificate): Boolean {
    // Verify if a whitelist entry exists
    return validityWhitelist.find(leafCertificate.fingerprint)?.let {
      it.ignoreDate || leafCertificate.isValid
    } ?: false
  }
}
