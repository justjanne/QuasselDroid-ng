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
    // First we normalize both by removing trailing dots (absolute DNS names), splitting into DNS
    // labels, and punycoding all unicode parts.
    val normalizedName = name.trimEnd('.').split('.').map(IDN::toASCII)
    val normalizedHost = host.trimEnd('.').split('.').map(IDN::toASCII)

    // Only if both have the same number of DNS labels they can match
    if (normalizedHost.size != normalizedName.size) return false

    // Hosts with size of zero are invalid
    if (normalizedHost.isEmpty()) return false

    val both = normalizedName.zip(normalizedHost)

    // The first label has to either match exactly, or be *
    if (!both.take(1).all { (target, actual) ->
        target.equals(actual, ignoreCase = true) || target == "*"
      }) return false

    // All other labels have to match exactly.
    if (!both.drop(1).all { (target, actual) ->
        target.equals(actual, ignoreCase = true)
      }) return false

    return true
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
