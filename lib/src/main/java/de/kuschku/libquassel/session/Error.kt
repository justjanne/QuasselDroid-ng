package de.kuschku.libquassel.session

import de.kuschku.libquassel.connection.QuasselSecurityException
import de.kuschku.libquassel.protocol.message.HandshakeMessage

sealed class Error {
  data class HandshakeError(val message: HandshakeMessage) : Error()
  data class SslError(val exception: QuasselSecurityException) : Error()
}
