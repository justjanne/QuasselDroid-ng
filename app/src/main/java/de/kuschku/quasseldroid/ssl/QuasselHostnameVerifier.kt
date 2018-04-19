package de.kuschku.quasseldroid.ssl

import de.kuschku.libquassel.connection.HostnameVerifier
import de.kuschku.libquassel.connection.QuasselSecurityException
import de.kuschku.libquassel.connection.SocketAddress
import de.kuschku.quasseldroid.ssl.custom.QuasselHostnameManager
import java.security.cert.X509Certificate
import javax.net.ssl.SSLException

class QuasselHostnameVerifier(
  private val hostnameManager: QuasselHostnameManager,
  private val hostnameVerifier: HostnameVerifier = BrowserCompatibleHostnameVerifier()
) : HostnameVerifier {
  override fun checkValid(address: SocketAddress, chain: Array<out X509Certificate>) {
    try {
      if (!hostnameManager.isValid(address, chain)) {
        hostnameVerifier.checkValid(address, chain)
      }
    } catch (e: SSLException) {
      throw QuasselSecurityException.Hostname(chain, address, e)
    }
  }
}
