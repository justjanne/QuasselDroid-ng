package de.justjanne.quasseldroid.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.justjanne.libquassel.protocol.features.FeatureSet
import de.justjanne.libquassel.protocol.models.ConnectedClient
import de.justjanne.libquassel.protocol.syncables.state.CoreInfoState
import org.threeten.bp.Instant

class SampleCoreInfoProvider : PreviewParameterProvider<CoreInfoState> {
  override val values = sequenceOf(
    CoreInfoState(
      version = "v0.14.0 (git-<a href=\"https://github.com/quassel/quassel/commit/da9c1c9fcf25f9dbd9acb96e6c8d1ff148e55986\">da9c1c9f</a>)",
      versionDate = Instant.ofEpochSecond(1645656060L),
      startTime = Instant.ofEpochSecond(1645656060L),
      connectedClientCount = 2,
      connectedClients = listOf(
        ConnectedClient(
          id = 5,
          remoteAddress = "192.168.178.1",
          location = "Kiel, Germany",
          version = "Quasseldroid <a href=\"https://git.kuschku.de/justJanne/QuasselDroid-ng/-/commit/afff49c2ae4be7717fa75f8c466d4f84b13641b5\">1.5.3-gafff49c2</a>",
          versionDate = Instant.ofEpochSecond(1645656060L),
          connectedSince = Instant.ofEpochSecond(1645656060L),
          secure = true,
          features = FeatureSet.all()
        ),
        ConnectedClient(
          id = 2,
          remoteAddress = "2a01:c22:bd32:4000:c7f:2640:7fcd:ae9c",
          location = "Kiel, Germany",
          version = "Quasseldroid <a href=\"https://git.kuschku.de/justJanne/QuasselDroid-ng/-/commit/afff49c2ae4be7717fa75f8c466d4f84b13641b5\">1.5.3-gafff49c2</a>",
          versionDate = Instant.ofEpochSecond(1645656060L),
          connectedSince = Instant.ofEpochSecond(1645656060L),
          secure = false,
          features = FeatureSet.all()
        )
      )
    )
  )
}
