package de.kuschku.quasseldroid.ssl

import de.kuschku.libquassel.connection.QuasselSecurityException
import de.kuschku.quasseldroid.ssl.custom.QuasselCertificateManager
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

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
