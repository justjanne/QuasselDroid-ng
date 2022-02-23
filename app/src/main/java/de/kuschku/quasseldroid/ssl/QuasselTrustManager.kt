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

package de.kuschku.quasseldroid.ssl

import android.annotation.SuppressLint
import de.kuschku.libquassel.connection.QuasselSecurityException
import de.kuschku.quasseldroid.ssl.custom.QuasselCertificateManager
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@SuppressLint("CustomX509TrustManager")
class QuasselTrustManager private constructor(
  private val certificateManager: QuasselCertificateManager,
  private val trustManager: X509TrustManager?
) : X509TrustManager {
  constructor(
    certificateManager: QuasselCertificateManager,
    factory: TrustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
      init(null as KeyStore?)
    }
  ) : this(
    certificateManager,
    factory.trustManagers.mapNotNull {
      it as? X509TrustManager
    }.firstOrNull()
  )

  override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    try {
      trustManager?.checkClientTrusted(chain, authType)
      ?: throw GeneralSecurityException("No TrustManager available")
    } catch (e: GeneralSecurityException) {
      throw QuasselSecurityException.Certificate(chain, e)
    }
  }

  override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    try {
      if (!certificateManager.isServerTrusted(chain)) {
        trustManager?.checkServerTrusted(chain, authType)
        ?: throw GeneralSecurityException("No TrustManager available")
      }
    } catch (e: GeneralSecurityException) {
      throw QuasselSecurityException.Certificate(chain, e)
    }
  }

  override fun getAcceptedIssuers(): Array<X509Certificate> =
    trustManager?.acceptedIssuers ?: emptyArray()
}
