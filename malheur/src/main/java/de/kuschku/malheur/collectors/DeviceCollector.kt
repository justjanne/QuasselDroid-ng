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
