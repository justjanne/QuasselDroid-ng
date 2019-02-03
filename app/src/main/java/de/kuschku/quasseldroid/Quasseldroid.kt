/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.quasseldroid

import android.content.Context
import dagger.android.support.DaggerApplication
import de.kuschku.quasseldroid.app.AppDelegate
import de.kuschku.quasseldroid.app.QuasseldroidReleaseDelegate
import de.kuschku.quasseldroid.dagger.DaggerAppComponent
import de.kuschku.quasseldroid.util.ui.LocaleHelper

open class Quasseldroid : DaggerApplication() {
  override fun applicationInjector() = DaggerAppComponent.builder().create(this)
  open val delegate: AppDelegate = QuasseldroidReleaseDelegate(this)

  override fun onCreate() {
    super.onCreate()
    if (delegate.shouldInit()) {
      delegate.onInit()
      applicationInjector().inject(this)
      delegate.onPostInit()
    }
  }

  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(LocaleHelper.setLocale(base))
    delegate.onInstallMultidex()
  }
}
