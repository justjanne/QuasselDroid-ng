package de.kuschku.libquassel.connection

import java.security.cert.X509Certificate
import javax.net.ssl.SSLException

interface HostnameVerifier {
  @Throws(SSLException::class)
  fun checkValid(address: SocketAddress, chain: Array<out X509Certificate>)
}
