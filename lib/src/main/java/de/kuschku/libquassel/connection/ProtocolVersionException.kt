package de.kuschku.libquassel.connection

import de.kuschku.libquassel.quassel.ProtocolInfo
import java.net.ConnectException

class ProtocolVersionException(val protocol: ProtocolInfo) : ConnectException() {
  override val message: String?
    get() = "Invalid Protocol Version: $protocol"
}
