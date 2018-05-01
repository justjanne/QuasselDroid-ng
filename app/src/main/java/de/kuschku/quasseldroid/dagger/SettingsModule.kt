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

package de.kuschku.quasseldroid.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import de.kuschku.quasseldroid.settings.Settings

@Module
class SettingsModule {
  @Provides
  fun provideAppearanceSettings(context: Context) = Settings.appearance(context)

  @Provides
  fun provideMessageSettings(context: Context) = Settings.message(context)

  @Provides
  fun provideNotificationSettings(context: Context) = Settings.notification(context)

  @Provides
  fun provideAutoCompleteSettings(context: Context) = Settings.autoComplete(context)

  @Provides
  fun provideBacklogSettings(context: Context) = Settings.backlog(context)

  @Provides
  fun provideConnectionSettings(context: Context) = Settings.connection(context)
}
