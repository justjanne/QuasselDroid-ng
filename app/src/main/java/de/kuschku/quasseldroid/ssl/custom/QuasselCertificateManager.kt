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
    val leafCertificate = chain?.lastOrNull() ?: return false
    return isServerTrusted(leafCertificate)
  }

  private fun isServerTrusted(leafCertificate: X509Certificate): Boolean {
    // Verify if a whitelist entry exists
    return validityWhitelist.find(leafCertificate.fingerprint)?.let {
      it.ignoreDate || leafCertificate.isValid
    } ?: false
  }
}
