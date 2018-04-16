package de.kuschku.libquassel.quassel.exceptions

import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage

sealed class MessageHandlingException(cause: Throwable?) : Exception(cause) {
  class SignalProxy(
    val source: SignalProxyMessage,
    cause: Throwable?
  ) : MessageHandlingException(cause)

  class Handshake(
    val source: HandshakeMessage,
    cause: Throwable?
  ) : MessageHandlingException(cause)
}
