package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.message.HandshakeMessage

interface AuthHandler {
  fun handle(f: HandshakeMessage.ClientInit) = false
  fun handle(f: HandshakeMessage.ClientInitReject) = false
  fun handle(f: HandshakeMessage.ClientInitAck) = false
  fun handle(f: HandshakeMessage.CoreSetupData) = false
  fun handle(f: HandshakeMessage.CoreSetupReject) = false
  fun handle(f: HandshakeMessage.CoreSetupAck) = false
  fun handle(f: HandshakeMessage.ClientLogin) = false
  fun handle(f: HandshakeMessage.ClientLoginReject) = false
  fun handle(f: HandshakeMessage.ClientLoginAck) = false
  fun handle(f: HandshakeMessage.SessionInit) = false

  fun handle(f: HandshakeMessage): Boolean = when (f) {
    is HandshakeMessage.ClientInit        -> handle(f)
    is HandshakeMessage.ClientInitReject  -> handle(f)
    is HandshakeMessage.ClientInitAck     -> handle(f)
    is HandshakeMessage.CoreSetupData     -> handle(f)
    is HandshakeMessage.CoreSetupReject   -> handle(f)
    is HandshakeMessage.CoreSetupAck      -> handle(f)
    is HandshakeMessage.ClientLogin       -> handle(f)
    is HandshakeMessage.ClientLoginReject -> handle(f)
    is HandshakeMessage.ClientLoginAck    -> handle(f)
    is HandshakeMessage.SessionInit       -> handle(f)
  }
}
