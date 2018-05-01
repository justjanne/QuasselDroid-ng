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

package de.kuschku.malheur.collectors

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.SparseArray
import android.view.Display
import android.view.WindowManager
import de.kuschku.malheur.CrashContext
import de.kuschku.malheur.data.DisplayInfo
import de.kuschku.malheur.data.MetricsInfo
import de.kuschku.malheur.util.getMetrics
import java.lang.reflect.Modifier

class DisplayCollector(application: Application) :
  Collector<DisplayInfo, Boolean> {
  private val windowManager = application.getSystemService(
    Context.WINDOW_SERVICE
  ) as WindowManager

  @Suppress("DEPRECATION")
  override fun collect(context: CrashContext, config: Boolean): DisplayInfo? {
    val display = windowManager.defaultDisplay
    val hdrCapabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      val capabilitiesEnum = getHdrCapabilitiesEnum()
      display.hdrCapabilities.supportedHdrTypes.map(capabilitiesEnum::get)
    } else {
      null
    }
    return DisplayInfo(
      width = display.width,
      height = display.height,
      pixelFormat = display.pixelFormat,
      refreshRate = display.refreshRate,
      hdr = hdrCapabilities,
      isWideGamut = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        display.isWideColorGamut
      } else {
        null
      },
      metrics = MetricsInfo(display.getMetrics())
    )
  }

  private fun getHdrCapabilitiesEnum(): SparseArray<String> {
    val hdrCapabilityEnums = SparseArray<String>()
    Display.HdrCapabilities::class.java.declaredFields.filter {
      Modifier.isStatic(it.modifiers)
    }.filter {
      it.name.startsWith("HDR_TYPE_")
    }.filter {
      it.type == Int::class.java
    }.forEach {
      try {
        val value = it.getInt(null)
        hdrCapabilityEnums.put(value, it.name.substring("HDR_TYPE_".length))
      } catch (e: IllegalAccessException) {
      }
    }
    return hdrCapabilityEnums
  }
}
