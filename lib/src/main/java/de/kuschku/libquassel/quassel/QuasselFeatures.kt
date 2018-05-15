/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

  infix fun hasFeature(feature: ExtendedFeature) = enabledFeatures.contains(feature)

  companion object {
    fun empty() = QuasselFeatures(emptySet(), emptySet())
    fun all() = QuasselFeatures(
      // Dcc is disabled
      ExtendedFeature.values().toSet() - ExtendedFeature.DccFileTransfer,
      emptySet()
    )
  }
}
