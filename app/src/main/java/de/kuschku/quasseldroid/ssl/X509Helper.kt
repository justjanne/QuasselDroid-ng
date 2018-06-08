package de.kuschku.quasseldroid.ssl

import java.security.cert.X509Certificate

object X509Helper {
  fun hostnames(certificate: X509Certificate): Sequence<String> =
    (sequenceOf(commonName(certificate)) + subjectAlternativeNames(certificate))
      .filterNotNull()
      .distinct()

  fun commonName(certificate: X509Certificate) =
    commonName(certificate.subjectX500Principal.name)

  fun commonName(distinguishedName: String) =
    COMMON_NAME.find(distinguishedName)?.groups?.get(1)?.value

  fun subjectAlternativeNames(certificate: X509Certificate): Sequence<String> =
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

  private val COMMON_NAME = Regex("""(?:^|,\s?)(?:CN=("(?:[^"]|"")+"|[^,]+))""")
}
