package de.kuschku.malheur.util

import android.util.DisplayMetrics
import android.view.Display

fun Display.getMetrics(): DisplayMetrics {
  val metrics = DisplayMetrics()
  getMetrics(metrics)
  return metrics
}
