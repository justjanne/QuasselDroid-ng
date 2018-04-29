/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
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

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.provider.Settings
import de.kuschku.malheur.CrashContext
import de.kuschku.malheur.config.DeviceConfig
import de.kuschku.malheur.data.DeviceInfo
import de.kuschku.malheur.util.reflectionCollectConstants
import java.io.File

class DeviceCollector(private val application: Application) : Collector<DeviceInfo, DeviceConfig> {
  private val displayCollector = DisplayCollector(application)

  @SuppressLint("HardwareIds")
  override fun collect(context: CrashContext, config: DeviceConfig): DeviceInfo {
    return DeviceInfo(
      build = collectIf(config.build) {
        reflectionCollectConstants(Build::class.java)
      },
      version = collectIf(config.version) {
        reflectionCollectConstants(Build.VERSION::class.java)
      },
      installationId = collectIf(config.installationId) {
        Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
      },
      processor = collectIf(config.processor) {
        readProcInfo()
      },
      display = displayCollector.collectIf(context, config.display)
    )
  }

  private fun readProcInfo() = File("/proc/cpuinfo")
    .bufferedReader(Charsets.UTF_8)
    .lineSequence()
    .map { line -> line.split(":") }
    .filter { split -> split.size == 2 }
    .map { (key, value) -> key.trim() to value.trim() }
    .filter { (key, _) -> key == "Hardware" }
    .map { (_, value) -> value }
    .firstOrNull()
}
