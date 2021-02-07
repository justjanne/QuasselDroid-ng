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

import de.kuschku.bitflags.none
import de.kuschku.bitflags.of
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FeatureSetTest {
  @Test
  fun testParse() {
    assertEquals(
      emptyList<QuasselFeatureName>(),
      FeatureSet.parse(
        LegacyFeature.none(),
        emptyList()
      ).featureList()
    )

    assertEquals(
      listOf(
        QuasselFeature.SynchronizedMarkerLine.feature,
        QuasselFeature.ExtendedFeatures.feature,
        QuasselFeatureName("_unknownFeature")
      ),
      FeatureSet.parse(
        LegacyFeature.of(
          LegacyFeature.SynchronizedMarkerLine
        ),
        listOf(
          QuasselFeature.ExtendedFeatures.feature,
          QuasselFeatureName("_unknownFeature")
        )
      ).featureList()
    )
  }

  @Test
  fun testBuild() {
    assertEquals(
      emptyList<QuasselFeatureName>(),
      FeatureSet.build(emptySet()).featureList()
    )

    assertEquals(
      listOf(
        QuasselFeature.SynchronizedMarkerLine.feature,
        QuasselFeature.ExtendedFeatures.feature
      ),
      FeatureSet.build(setOf(
        QuasselFeature.SynchronizedMarkerLine,
        QuasselFeature.ExtendedFeatures
      )).featureList()
    )
  }
}
