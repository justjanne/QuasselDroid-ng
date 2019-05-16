/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.malheur.collectors

import android.app.Application
import android.os.Build
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
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        application.packageManager.getPackageInfo(application.packageName, 0).longVersionCode
      } else {
        @Suppress("DEPRECATION")
        application.packageManager.getPackageInfo(application.packageName, 0).versionCode.toLong()
      }
    },
    buildConfig = collectIf(config.buildConfig) {
      reflectionCollectConstants(
        context.buildConfig ?: getBuildConfigClass(application.packageName)
      )
    },
    installationSource = collectIf(config.installationSource) {
      application.packageManager.getInstallerPackageName(application.packageName)
    }
  )

  private fun getBuildConfigClass(packageName: String) = try {
    Class.forName("$packageName.BuildConfig")
  } catch (e: ClassNotFoundException) {
    null
  }
}
