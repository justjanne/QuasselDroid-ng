package de.kuschku.malheur.data

data class DisplayInfo(
  val width: Int,
  val height: Int,
  val pixelFormat: Int,
  val refreshRate: Float,
  val hdr: List<String>?,
  val isWideGamut: Boolean?,
  val metrics: MetricsInfo
)
