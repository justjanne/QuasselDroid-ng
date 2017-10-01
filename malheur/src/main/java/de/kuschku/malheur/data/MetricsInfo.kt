package de.kuschku.malheur.data

import android.util.DisplayMetrics

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
