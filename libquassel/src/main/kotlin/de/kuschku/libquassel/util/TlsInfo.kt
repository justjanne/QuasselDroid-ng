/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
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

package de.kuschku.quasseldroid.util

import java.security.cert.Certificate
import java.security.cert.X509Certificate
import javax.net.ssl.SSLSession

data class TlsInfo(
  val protocol: String,
  val cipherSuite: String,
  val keyExchangeMechanism: String?,
  val certificateChain: List<X509Certificate>,
) {

  override fun toString(): String {
    return "TlsInfo(protocol='$protocol', cipherSuite='$cipherSuite', keyExchangeMechanism=$keyExchangeMechanism)"
  }

  companion object {
    private val cipherSuiteRegex13 = "TLS_(.*)".toRegex()
    private val cipherSuiteRegex12 = "TLS_(.*)_WITH_(.*)".toRegex()

    private fun cipherSuiteRegex(protocol: String): Regex =
      if (protocol == "TLSv1.3") cipherSuiteRegex13
      else cipherSuiteRegex12

    private fun parseCipherSuite(protocol: String, cipherSuite: String): Pair<String, String?>? {
      val match = cipherSuiteRegex(protocol)
        .matchEntire(cipherSuite)
        ?: return null

      return if (protocol == "TLSv1.3") {
        Pair(match.groupValues[1], null)
      } else {
        Pair(match.groupValues[1], match.groupValues.getOrNull(2))
      }
    }

    fun ofSession(session: SSLSession): TlsInfo? {
      val (cipherSuite, keyExchangeMechanism) = parseCipherSuite(
        session.protocol,
        session.cipherSuite,
      ) ?: return null

      return TlsInfo(
        session.protocol,
        cipherSuite,
        keyExchangeMechanism,
        session.peerCertificates.map(Certificate::toX509)
      )
    }
  }
}
