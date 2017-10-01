package de.kuschku.malheur.collectors

import android.app.Application
import de.kuschku.malheur.CrashContext
import de.kuschku.malheur.config.AppConfig
import de.kuschku.malheur.data.AppInfo
import de.kuschku.malheur.util.reflectionCollectConstants

class AppCollector(private val application: Application) : Collector<AppInfo, AppConfig> {
  override fun collect(context: CrashContext, config: AppConfig) = AppInfo(
    versionName = collectIf(config.versionName) {
      application.packageManager.getPackageInfo(application.packageName, 0).versionName
    },
    versionCode = collectIf(config.versionCode) {
      application.packageManager.getPackageInfo(application.packageName, 0).versionCode
    },
    buildConfig = collectIf(config.buildConfig) {
      reflectionCollectConstants(
        context.buildConfig ?: getBuildConfigClass(application.packageName)
      )
    }
  )

  private fun getBuildConfigClass(packageName: String) = try {
    Class.forName("$packageName.BuildConfig")
  } catch (e: ClassNotFoundException) {
    null
  }
}
