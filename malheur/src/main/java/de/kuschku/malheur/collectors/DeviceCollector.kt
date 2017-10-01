package de.kuschku.malheur.collectors

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.provider.Settings
import de.kuschku.malheur.CrashContext
import de.kuschku.malheur.config.DeviceConfig
import de.kuschku.malheur.data.DeviceInfo
import de.kuschku.malheur.util.readProcInfo
import de.kuschku.malheur.util.reflectionCollectConstants

class DeviceCollector(private val application: Application) : Collector<DeviceInfo, DeviceConfig> {
  @SuppressLint("HardwareIds")
  override fun collect(context: CrashContext, config: DeviceConfig) = DeviceInfo(
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
    }
  )
}
