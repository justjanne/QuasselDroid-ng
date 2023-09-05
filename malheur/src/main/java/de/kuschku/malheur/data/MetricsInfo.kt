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

package de.kuschku.malheur.data

import android.util.DisplayMetrics
import kotlinx.serialization.Serializable

@Serializable
data class MetricsInfo(
  val density: Float,
  val scaledDensity: Float,
  val widthPixels: Int,
  val heightPixels: Int,
  val xdpi: Float,
  val ydpi: Float
) {
  constructor(metrics: DisplayMetrics) : this(
    density = metrics.density,
    scaledDensity = metrics.scaledDensity,
    widthPixels = metrics.widthPixels,
    heightPixels = metrics.heightPixels,
    xdpi = metrics.xdpi,
    ydpi = metrics.ydpi
  )
}
