package de.kuschku.libquassel.quassel

import de.kuschku.libquassel.protocol.Legacy_Feature
import de.kuschku.libquassel.protocol.Legacy_Features

class QuasselFeatures(
  val enabledFeatures: Set<ExtendedFeature>,
  val unknownFeatures: Set<String>
) {
  constructor(legacyFeatures: Legacy_Features?, extendedFeatures: Collection<String>) : this(
    legacyFeatures?.enabledValues()?.map(Legacy_Feature::toExtended).orEmpty() union
      extendedFeatures.mapNotNull { ExtendedFeature.of(it) },
    extendedFeatures.filter { ExtendedFeature.of(it) == null }.toSet()
  )

  fun toInt() = LegacyFeature.of(enabledFeatures.map(LegacyFeature.Companion::fromExtended))

  fun toStringList() = enabledFeatures.map(ExtendedFeature::name)

  fun hasFeature(feature: ExtendedFeature) = enabledFeatures.contains(feature)

  companion object {
    fun empty() = QuasselFeatures(emptySet(), emptySet())
    fun all() = QuasselFeatures(ExtendedFeature.values().toSet(), emptySet())
  }
}

