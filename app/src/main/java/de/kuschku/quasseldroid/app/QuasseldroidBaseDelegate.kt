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

package de.kuschku.quasseldroid.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import de.kuschku.quasseldroid.BuildConfig
import de.kuschku.quasseldroid.Quasseldroid
import de.kuschku.quasseldroid.util.backport.AndroidThreeTenBackport
import de.kuschku.quasseldroid.util.compatibility.AndroidCompatibilityUtils
import de.kuschku.quasseldroid.util.compatibility.AndroidLoggingHandler
import de.kuschku.quasseldroid.util.compatibility.AndroidStreamChannelFactory

open class QuasseldroidBaseDelegate(private val app: Quasseldroid) : AppDelegate {
  override fun shouldInit() = true

  override fun onPreInit() = Unit

  override fun onInit() {
    // Init compatibility utils
    AndroidCompatibilityUtils.inject()
    AndroidLoggingHandler.inject()
    AndroidStreamChannelFactory.inject()

    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

    AndroidThreeTenBackport.init(app)
  }

  override fun onPostInit() = Unit

  override fun onAttachBaseContext() {
    if (BuildConfig.DEBUG) {
      MultiDex.install(app)
    }
  }
}
