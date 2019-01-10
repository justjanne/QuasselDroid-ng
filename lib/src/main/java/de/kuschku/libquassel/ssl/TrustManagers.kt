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

package de.kuschku.libquassel.ssl

import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object TrustManagers {
  fun default() = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
    init(null as KeyStore?)
  }.trustManagers.mapNotNull {
    it as? X509TrustManager
  }.firstOrNull() ?: throw GeneralSecurityException("No TrustManager available")

  fun trustAll() = TrustAllX509TrustManager()

  class TrustAllX509TrustManager : X509TrustManager {
    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) = Unit
    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) = Unit
    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
  }
}
