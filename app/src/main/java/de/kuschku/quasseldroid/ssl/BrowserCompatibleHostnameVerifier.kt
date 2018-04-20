package de.kuschku.quasseldroid.ssl

import de.kuschku.libquassel.connection.HostnameVerifier
import de.kuschku.libquassel.connection.SocketAddress
import java.net.IDN
import java.security.cert.X509Certificate
import javax.net.ssl.SSLException

class BrowserCompatibleHostnameVerifier : HostnameVerifier {
  override fun checkValid(address: SocketAddress, chain: Array<out X509Certificate>) {
    val leafCertificate = chain.firstOrNull() ?: throw SSLException("No Certificate found")
    val hostnames = hostnames(leafCertificate).toList()
    if (hostnames.none { matches(it, address.host) })
      throw SSLException("Hostname does not match")
  }

  private fun matches(name: String, host: String): Boolean {
    val normalizedName = IDN.toASCII(name).trimEnd('.')
    val normalizedHost = IDN.toASCII(host).trimEnd('.')
    return normalizedName.equals(normalizedHost, ignoreCase = true)
  }

  private fun hostnames(certificate: X509Certificate): Sequence<String> =
    (sequenceOf(commonName(certificate)) + subjectAlternativeNames(certificate))
      .filterNotNull()
      .distinct()

  private fun commonName(certificate: X509Certificate): String? {
    return COMMON_NAME.find(certificate.subjectX500Principal.name)?.groups?.get(1)?.value
  }

  private fun subjectAlternativeNames(certificate: X509Certificate): Sequence<String> =
    certificate.subjectAlternativeNames.orEmpty().asSequence().mapNotNull {
      val type = it[0] as? Int
      val name = it[1] as? String
      if (type != null && name != null) Pair(type, name)
      else null
    }.filter { (type, _) ->
      // 2 is DNS Name
      type == 2
    }.map { (_, name) ->
      name
    }

  companion object {
    private val COMMON_NAME = Regex("""(?:^|,\s?)(?:CN=("(?:[^"]|"")+"|[^,]+))""")
  }
}
