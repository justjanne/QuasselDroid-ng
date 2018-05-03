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

package de.kuschku.quasseldroid.util.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import de.kuschku.quasseldroid.settings.Settings
import java.util.*


object LocaleHelper {
  fun setLocale(context: Context): Context {
    return updateResources(context, Settings.appearance(context).language)
  }

  private fun updateResources(context: Context, language: String) = if (language.isNotEmpty()) {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      config.setLocale(locale)
      context.createConfigurationContext(config)
    } else {
      config.locale = locale
      context.resources.updateConfiguration(config, context.resources.displayMetrics)
      context
    }
  } else context
}
