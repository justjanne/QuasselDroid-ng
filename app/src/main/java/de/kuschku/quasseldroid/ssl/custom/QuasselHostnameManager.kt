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
    val all = hostnameWhitelist.all()
    return whitelistEntry != null
  }
}
