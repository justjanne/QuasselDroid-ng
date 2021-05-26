/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.libquassel.connection

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

  companion object {
    fun empty() = Features(QuasselFeatures.empty(), QuasselFeatures.empty())
    fun all() = Features(QuasselFeatures.all(), QuasselFeatures.all())
  }
}