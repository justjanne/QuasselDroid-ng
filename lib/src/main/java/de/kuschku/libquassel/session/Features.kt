package de.kuschku.libquassel.session

import de.kuschku.libquassel.quassel.QuasselFeatures

data class Features(
  var client: QuasselFeatures,
  var core: QuasselFeatures
) {
  val negotiated: QuasselFeatures
    get() = QuasselFeatures(
      core.enabledFeatures intersect client.enabledFeatures,
      core.unknownFeatures union client.unknownFeatures
    )
}
