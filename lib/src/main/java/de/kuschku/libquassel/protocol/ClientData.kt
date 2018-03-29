package de.kuschku.libquassel.protocol

import de.kuschku.libquassel.quassel.QuasselFeatures
import org.threeten.bp.Instant

data class ClientData(
  val identifier: String,
  val buildDate: Instant,
  val clientFeatures: QuasselFeatures,
  val protocolFeatures: Protocol_Features,
  val supportedProtocols: List<Protocol>
)
