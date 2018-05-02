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

package de.kuschku.quasseldroid

import android.os.Build
import android.os.StrictMode
import android.support.v7.preference.PreferenceManager
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import de.kuschku.malheur.CrashHandler
import de.kuschku.quasseldroid.dagger.DaggerAppComponent
import de.kuschku.quasseldroid.persistence.AccountDatabase
import de.kuschku.quasseldroid.persistence.LegacyAccountDatabase
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.SettingsMigration
import de.kuschku.quasseldroid.settings.SettingsMigrationManager
import de.kuschku.quasseldroid.util.backport.AndroidThreeTenBackport
import de.kuschku.quasseldroid.util.compatibility.AndroidCompatibilityUtils
import de.kuschku.quasseldroid.util.compatibility.AndroidLoggingHandler
import de.kuschku.quasseldroid.util.compatibility.AndroidStreamChannelFactory

class Quasseldroid : DaggerApplication() {
  override fun applicationInjector(): AndroidInjector<Quasseldroid> =
    DaggerAppComponent.builder().create(this)

  override fun onCreate() {
    super.onCreate()
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return
    }
    LeakCanary.install(this)
    // Normal app init code...

    CrashHandler.init(
      application = this,
      buildConfig = BuildConfig::class.java
    )

    // Init compatibility utils
    AndroidCompatibilityUtils.inject()
    AndroidLoggingHandler.inject()
    AndroidStreamChannelFactory.inject()

    AndroidThreeTenBackport.init(this)

    applicationInjector().inject(this)

    // Migrate preferences
    SettingsMigrationManager(listOf(
      SettingsMigration.migrationOf(0, 1) { prefs, edit ->
        // Migrating database
        val database = LegacyAccountDatabase.Creator.init(this)
        val accounts = database.accounts().all()
        database.close()

        val accountDatabase = AccountDatabase.Creator.init(this)
        accountDatabase.accounts().create(*accounts.map {
          AccountDatabase.Account(
            id = it.id,
            host = it.host,
            port = it.port,
            user = it.user,
            pass = it.pass,
            name = it.name,
            lastUsed = 0
          )
        }.toTypedArray())
        Thread(Runnable {
          deleteDatabase("data")
        }).start()

        // Migrating actual settings
        if (prefs.contains("selectedtheme")) {
          prefs.getString("selectedtheme", "").let { theme ->
            when (theme) {
              "light" -> AppearanceSettings.Theme.MATERIAL_LIGHT
              "dark"  -> AppearanceSettings.Theme.MATERIAL_DARK
              else    -> null
            }?.let {
              edit.putString(getString(R.string.preference_theme_key), it.name)
            }
          }
          edit.remove("selectedtheme")
        }

        if (prefs.contains("timestamp")) {
          prefs.getString("timestamp", "").let { timestamp ->
            edit.putBoolean(getString(R.string.preference_show_seconds_key),
                            timestamp.contains("ss"))
            edit.putBoolean(getString(R.string.preference_show_seconds_key),
                            timestamp.contains("hh") || timestamp.contains("a"))
          }
          edit.remove("timestamp")
        }

        if (prefs.contains("fontsizeChannelList")) {
          prefs.getString("fontsizeChannelList", "").toIntOrNull()?.let { fontSize ->
            edit.putInt(getString(R.string.preference_textsize_key), fontSize)
          }
          edit.remove("fontsizeChannelList")
        }

        if (prefs.contains("allowcoloredtext")) {
          prefs.getBoolean("allowcoloredtext", false).let {
            edit.putBoolean(getString(R.string.preference_colorize_mirc_key), it)
          }
          edit.remove("allowcoloredtext")
        }

        if (prefs.contains("monospace")) {
          prefs.getBoolean("monospace", false).let {
            edit.putBoolean(getString(R.string.preference_monospace_key), it)
          }
          edit.remove("monospace")
        }

        if (prefs.contains("detailed_actions")) {
          prefs.getBoolean("detailed_actions", false).let {
            edit.putBoolean(getString(R.string.preference_hostmask_actions_key), it)
          }
          edit.remove("detailed_actions")
        }

        if (prefs.contains("showlag")) {
          prefs.getBoolean("showlag", false).let {
            edit.putBoolean(getString(R.string.preference_show_lag_key), it)
          }
          edit.remove("showlag")
        }
      }
    )).migrate(PreferenceManager.getDefaultSharedPreferences(this))

    // Initialize preferences unless already set
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

    /*
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      systemService<ShortcutManager>().dynamicShortcuts = listOf(
        ShortcutInfo.Builder(this, "id1")
          .setShortLabel("#quassel")
          .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_channel))
          .setIntent(packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID))
          .build(),
        ShortcutInfo.Builder(this, "id2")
          .setShortLabel("#quasseldroid")
          .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_channel))
          .setIntent(packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID))
          .build(),
        ShortcutInfo.Builder(this, "id3")
          .setShortLabel("#quassel.de")
          .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_channel))
          .setIntent(packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID))
          .build(),
        ShortcutInfo.Builder(this, "id4")
          .setShortLabel("justJanne")
          .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_query))
          .setIntent(packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID))
          .build()
      )
    }
    */

    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
          .detectNetwork()
          .detectCustomSlowCalls()
          .let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              it.detectResourceMismatches()
            } else {
              it
            }
          }.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              it.detectUnbufferedIo()
            } else {
              it
            }
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
          .let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
              it.detectFileUriExposure()
            } else {
              it
            }
          }.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              it.detectContentUriWithoutPermission()
            } else {
              it
            }
          }
          .penaltyLog()
          .build()
      )
    }
  }
}
