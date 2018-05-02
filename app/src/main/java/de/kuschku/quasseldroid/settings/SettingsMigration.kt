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

import android.content.SharedPreferences

interface SettingsMigration {
  val from: Int
  val to: Int

  fun migrate(preferences: SharedPreferences, editor: SharedPreferences.Editor)

  companion object {
    fun migrationOf(from: Int, to: Int,
                    migrationFunction: (SharedPreferences, SharedPreferences.Editor) -> Unit): SettingsMigration {
      return object : SettingsMigration {
        override val from = from
        override val to = to
        override fun migrate(preferences: SharedPreferences, editor: SharedPreferences.Editor) =
          migrationFunction(preferences, editor)
      }
    }
  }
}
