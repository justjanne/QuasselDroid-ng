package de.kuschku.libquassel.connection

import java.security.GeneralSecurityException
import java.security.cert.X509Certificate

sealed class QuasselSecurityException(
  val certificateChain: Array<out X509Certificate>?,
  cause: Throwable
) : GeneralSecurityException(cause) {
  class Certificate(
    certificateChain: Array<out X509Certificate>?,
    cause: Exception
  ) : QuasselSecurityException(certificateChain, cause)

  class Hostname(
    certificateChain: Array<out X509Certificate>?,
    val address: SocketAddress,
    cause: Exception
  ) : QuasselSecurityException(certificateChain, cause)
}
