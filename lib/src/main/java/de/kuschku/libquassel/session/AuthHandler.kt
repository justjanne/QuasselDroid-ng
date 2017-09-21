package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.HandshakeMessage

interface AuthHandler {
  fun handle(function: HandshakeMessage.ClientInit) {}
  fun handle(function: HandshakeMessage.ClientInitReject) {}
  fun handle(function: HandshakeMessage.ClientInitAck) {}
  fun handle(function: HandshakeMessage.CoreSetupData) {}
  fun handle(function: HandshakeMessage.CoreSetupReject) {}
  fun handle(function: HandshakeMessage.CoreSetupAck) {}
  fun handle(function: HandshakeMessage.ClientLogin) {}
  fun handle(function: HandshakeMessage.ClientLoginReject) {}
  fun handle(function: HandshakeMessage.ClientLoginAck) {}
  fun handle(function: HandshakeMessage.SessionInit) {}

  fun handle(function: HandshakeMessage) = when (function) {
    is HandshakeMessage.ClientInit        -> handle(function)
    is HandshakeMessage.ClientInitReject  -> handle(function)
    is HandshakeMessage.ClientInitAck     -> handle(function)
    is HandshakeMessage.CoreSetupData     -> handle(function)
    is HandshakeMessage.CoreSetupReject   -> handle(function)
    is HandshakeMessage.CoreSetupAck      -> handle(function)
    is HandshakeMessage.ClientLogin       -> handle(function)
    is HandshakeMessage.ClientLoginReject -> handle(function)
    is HandshakeMessage.ClientLoginAck    -> handle(function)
    is HandshakeMessage.SessionInit       -> handle(function)
  }
}
