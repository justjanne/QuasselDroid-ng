package de.kuschku.libquassel.protocol

import org.threeten.bp.Instant

data class ClientData(
  val identifier: String,
  val buildDate: Instant,
  val clientFeatures: Quassel_Features,
  val protocolFeatures: Protocol_Features,
  val supportedProtocols: List<Protocol>
)
