package de.justjanne.quasseldroid.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.justjanne.libquassel.protocol.features.FeatureSet
import de.justjanne.libquassel.protocol.models.ConnectedClient
import org.threeten.bp.Instant

class SampleConnectedClientProvider : PreviewParameterProvider<ConnectedClient> {
  override val values = sequenceOf(
    ConnectedClient(
      id = 5,
      remoteAddress = "192.168.178.1",
      location = "Kiel, Germany",
      version = "Quasseldroid 1.5.3-gafff49c2",
      versionDate = Instant.ofEpochSecond(1645656060L),
      connectedSince = Instant.ofEpochSecond(1645656060L),
      secure = true,
      features = FeatureSet.all()
    ),
    ConnectedClient(
      id = 2,
      remoteAddress = "2a01:c22:bd32:4000:c7f:2640:7fcd:ae9c",
      location = "Kiel, Germany",
      version = "Quasseldroid 1.5.3-gafff49c2",
      versionDate = Instant.ofEpochSecond(1645656060L),
      connectedSince = Instant.ofEpochSecond(1645656060L),
      secure = false,
      features = FeatureSet.all()
    )
  )
}
