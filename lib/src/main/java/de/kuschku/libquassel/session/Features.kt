package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.Quassel_Features
import de.kuschku.libquassel.util.and

data class Features(
  var client: Quassel_Features,
  var core: Quassel_Features
) {
  val negotiated: Quassel_Features
    get() = core and client
}