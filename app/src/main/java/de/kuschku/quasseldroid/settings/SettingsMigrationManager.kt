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

package de.kuschku.quasseldroid.settings

import android.annotation.SuppressLint
import android.content.SharedPreferences

class SettingsMigrationManager(
  migrations: List<SettingsMigration>
) {
  private val migrationMap = migrations.associateBy(SettingsMigration::from)
  private val currentVersion = migrations.map(SettingsMigration::to).max()

  // This runs during initial start and has to run synchronously
  @SuppressLint("ApplySharedPref")
  fun migrate(preferences: SharedPreferences) {
    var version = preferences.getInt(SETTINGS_VERSION, 0)
    while (version != currentVersion) {
      val migration = migrationMap[version]
                      ?: throw IllegalArgumentException("Migration not available")
      val editor = preferences.edit()
      migration.migrate(preferences, editor)
      version = migration.to
      editor.putInt(SETTINGS_VERSION, version)
      editor.commit()
    }
  }

  companion object {
    private const val SETTINGS_VERSION = "settings_version"
  }
}
