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
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import de.kuschku.libquassel.util.helper.nullIf
import de.kuschku.quasseldroid.settings.AppearanceSettings
import javax.inject.Inject

abstract class ThemedActivity : AppCompatActivity(), HasAndroidInjector {
  @Inject
  lateinit var androidInjector: DispatchingAndroidInjector<Any>

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    setTheme(appearanceSettings.theme.style)
    super.onCreate(savedInstanceState)
    packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA).labelRes
      .nullIf { it == 0 }?.let(this::setTitle)
  }

  override fun attachBaseContext(newBase: Context) {
    super.attachBaseContext(LocaleHelper.setLocale(newBase))
  }

  override fun androidInjector(): AndroidInjector<Any> {
    return androidInjector
  }
}
