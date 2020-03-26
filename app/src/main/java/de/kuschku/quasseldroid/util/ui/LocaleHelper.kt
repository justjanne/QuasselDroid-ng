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

package de.kuschku.quasseldroid.util.ui

import android.content.Context
import android.content.res.Configuration
import de.kuschku.quasseldroid.settings.Settings
import java.util.*


object LocaleHelper {
  fun setLocale(context: Context): Context {
    return updateResources(context, Settings.appearance(context).language)
  }

  fun parseLanguageCode(rawLanguage: String): Locale {
    val split = rawLanguage.split("-", limit = 3)
    return when (split.size) {
      3    -> Locale(split[0], split[2], split[1])
      2    -> Locale(split[0], split[1], "")
      else -> Locale(split[0], "", "")
    }
  }

  private fun updateResources(context: Context, language: String) = if (language.isNotEmpty()) {
    val locale = parseLanguageCode(language)
    Locale.setDefault(locale)
    context.createConfigurationContext(Configuration(context.resources.configuration).apply {
      setLocale(locale)
    })
  } else context
}
