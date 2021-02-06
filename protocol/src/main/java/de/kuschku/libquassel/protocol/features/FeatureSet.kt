/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.features

import de.kuschku.bitflags.flags

class FeatureSet internal constructor(
  private val features: Set<QuasselFeature>,
  private val additional: Set<QuasselFeatureName> = emptySet()
) {
  fun enabled(feature: QuasselFeature) = features.contains(feature)

  fun featureList(): List<QuasselFeatureName> =
    features.map(QuasselFeature::feature) + additional

  fun legacyFeatures(): LegacyFeatures =
    flags(features.mapNotNull(LegacyFeature.Companion::get))

  companion object {
    fun parse(
      legacy: LegacyFeatures,
      features: Collection<QuasselFeatureName>
    ) = FeatureSet(
      features = parseFeatures(legacy) + parseFeatures(features),
      additional = unknownFeatures(features)
    )

    fun build(vararg features: QuasselFeature) = FeatureSet(features.toSet())
    fun build(features: Set<QuasselFeature>) = FeatureSet(features)
    fun all() = build(*QuasselFeature.values())
    fun empty() = build()

    private fun parseFeatures(features: LegacyFeatures) =
      features.map(LegacyFeature::feature).toSet()

    private fun parseFeatures(features: Collection<QuasselFeatureName>) =
      features.mapNotNull(QuasselFeature.Companion::valueOf).toSet()

    private fun unknownFeatures(features: Collection<QuasselFeatureName>) =
      features.filter { QuasselFeature.valueOf(it) == null }.toSet()
  }
}
