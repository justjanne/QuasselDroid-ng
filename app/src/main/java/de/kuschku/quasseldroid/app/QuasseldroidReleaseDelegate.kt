/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.quasseldroid.app

import android.annotation.SuppressLint
import android.os.Build
import android.os.StrictMode
import com.squareup.leakcanary.LeakCanary
import de.kuschku.malheur.CrashHandler
import de.kuschku.quasseldroid.BuildConfig
import de.kuschku.quasseldroid.Quasseldroid
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.dao.create
import de.kuschku.quasseldroid.persistence.db.AccountDatabase
import de.kuschku.quasseldroid.persistence.db.LegacyAccountDatabase
import de.kuschku.quasseldroid.persistence.models.Account
import de.kuschku.quasseldroid.persistence.util.AccountId
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.SettingsMigration
import de.kuschku.quasseldroid.settings.SettingsMigrationManager
import de.kuschku.quasseldroid.util.helper.letIf

class QuasseldroidReleaseDelegate(private val app: Quasseldroid) : QuasseldroidBaseDelegate(app) {
  override fun shouldInit() = !LeakCanary.isInAnalyzerProcess(app)

  override fun onPreInit() {
    LeakCanary.install(app)
    CrashHandler.init<BuildConfig>(application = app)
  }

  @SuppressLint("NewApi")
  override fun onPostInit() {
    // Migrate preferences
    SettingsMigrationManager(
      listOf(
        SettingsMigration.migrationOf(0, 1) { prefs, edit ->
          // Migrating database
          val database = LegacyAccountDatabase.Creator.init(app)
          val accounts = database.accounts().all()
          database.close()

          val accountDatabase = AccountDatabase.Creator.init(app)
          accountDatabase.accounts().create(*accounts.map {
            Account.of(
              id = AccountId(it.id),
              host = it.host,
              port = it.port,
              user = it.user,
              requireSsl = false,
              pass = it.pass,
              name = it.name,
              lastUsed = 0,
              acceptedMissingFeatures = false,
              defaultFiltered = 0
            )
          }.toTypedArray())
          Thread(Runnable {
            app.deleteDatabase("data")
          }).start()

          // Migrating actual settings
          if (prefs.contains("selectedtheme")) {
            prefs.getString("selectedtheme", "").let { theme ->
              when (theme) {
                "light" -> AppearanceSettings.Theme.MATERIAL_LIGHT
                "dark"  -> AppearanceSettings.Theme.MATERIAL_DARK
                else    -> null
              }?.let {
                edit.putString(app.getString(R.string.preference_theme_key),
                               it.name)
              }
            }
            edit.remove("selectedtheme")
          }

          if (prefs.contains("timestamp")) {
            prefs.getString("timestamp", "").let {
              val timestamp = it ?: ""
              edit.putBoolean(app.getString(R.string.preference_show_seconds_key),
                              timestamp.contains("ss"))
              edit.putBoolean(app.getString(R.string.preference_show_seconds_key),
                              !timestamp.contains("hh") && !timestamp.contains("a"))
            }
            edit.remove("timestamp")
          }

          if (prefs.contains("fontsizeChannelList")) {
            prefs.getString("fontsizeChannelList", "")?.toIntOrNull()?.let { fontSize ->
              edit.putInt(app.getString(R.string.preference_textsize_key),
                          fontSize)
            }
            edit.remove("fontsizeChannelList")
          }

          if (prefs.contains("allowcoloredtext")) {
            prefs.getBoolean("allowcoloredtext", false).let {
              edit.putBoolean(app.getString(R.string.preference_colorize_mirc_key),
                              it)
            }
            edit.remove("allowcoloredtext")
          }

          if (prefs.contains("monospace")) {
            prefs.getBoolean("monospace", false).let {
              edit.putBoolean(app.getString(R.string.preference_monospace_key),
                              it)
            }
            edit.remove("monospace")
          }

          if (prefs.contains("detailed_actions")) {
            prefs.getBoolean("detailed_actions", false).let {
              edit.putBoolean(app.getString(R.string.preference_hostmask_actions_key),
                              it)
            }
            edit.remove("detailed_actions")
          }

          if (prefs.contains("showlag")) {
            prefs.getBoolean("showlag", false).let {
              edit.putBoolean(app.getString(R.string.preference_show_lag_key),
                              it)
            }
            edit.remove("showlag")
          }
        }
      )
    ).migrate(app)

    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
          .detectNetwork()
          .detectCustomSlowCalls()
          .letIf(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            it.detectResourceMismatches()
          }.letIf(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it.detectUnbufferedIo()
          }
          .penaltyLog()
          .build()
      )
      StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
          .detectLeakedSqlLiteObjects()
          .detectActivityLeaks()
          .detectLeakedClosableObjects()
          .detectLeakedRegistrationObjects()
          .detectFileUriExposure()
          .letIf(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            it.detectContentUriWithoutPermission()
          }
          .penaltyLog()
          .build()
      )
    }
  }
}
