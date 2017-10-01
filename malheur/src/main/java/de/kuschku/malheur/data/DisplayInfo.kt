package de.kuschku.malheur.data

import android.view.Display
import de.kuschku.malheur.util.getMetrics

data class DisplayInfo(
  val width: Int,
  val height: Int,
  val pixelFormat: Int,
  val refreshRate: Float,
  val isHdr: Boolean,
  val isWideGamut: Boolean,
  val metrics: MetricsInfo
) {
  constructor(display: Display) : this(
    width = display.width,
    height = display.height,
    pixelFormat = display.pixelFormat,
    refreshRate = display.refreshRate,
    isHdr = display.isHdr,
    isWideGamut = display.isWideColorGamut,
    metrics = MetricsInfo(display.getMetrics())
  )
}
