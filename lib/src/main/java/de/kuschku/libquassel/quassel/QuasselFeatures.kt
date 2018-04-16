package de.kuschku.libquassel.quassel

import de.kuschku.libquassel.protocol.Legacy_Feature
import de.kuschku.libquassel.protocol.Legacy_Features

class QuasselFeatures(
  val enabledFeatures: Set<ExtendedFeature>,
  val unknownFeatures: Set<String> = emptySet()
) {
  constructor(legacyFeatures: Legacy_Features?, extendedFeatures: Collection<String>) : this(
    legacyFeatures?.enabledValues()?.map(Legacy_Feature::toExtended).orEmpty() union
      extendedFeatures.mapNotNull(ExtendedFeature.Companion::of),
    extendedFeatures.filter { ExtendedFeature.of(it) == null }.toSet()
  )

  fun toInt() = LegacyFeature.of(enabledFeatures.mapNotNull(LegacyFeature.Companion::fromExtended))

  fun toStringList() = enabledFeatures.map(ExtendedFeature::name)

  fun hasFeature(feature: ExtendedFeature) = enabledFeatures.contains(feature)

  companion object {
    fun empty() = QuasselFeatures(emptySet(), emptySet())
    fun all() = QuasselFeatures(ExtendedFeature.values().toSet(), emptySet())
  }
}
