/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

// FIXME: re-read RFC and check it's actually secure
object X509Helper {
  val certificateFactory = CertificateFactory.getInstance("X.509")

  fun hostnames(certificate: X509Certificate): Sequence<String> =
    (sequenceOf(certificate.subjectX500Principal.commonName) + subjectAlternativeNames(certificate))
      .filterNotNull()
      .distinct()

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

  fun convert(certificate: javax.security.cert.X509Certificate) =
    certificateFactory.generateCertificate(ByteArrayInputStream(certificate.encoded)) as? X509Certificate


  val COMMON_NAME = Regex("""(?:^|,\s?)(?:CN=("(?:[^"]|"")+"|[^,]+))""")
  val ORGANIZATION = Regex("""(?:^|,\s?)(?:O=("(?:[^"]|"")+"|[^,]+))""")
  val ORGANIZATIONAL_UNIT = Regex("""(?:^|,\s?)(?:OU=("(?:[^"]|"")+"|[^,]+))""")
}
