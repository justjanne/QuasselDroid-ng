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

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import dagger.android.support.HasSupportFragmentInjector
import de.kuschku.quasseldroid.settings.AppearanceSettings
import javax.inject.Inject

abstract class ThemedActivity : AppCompatActivity(), HasSupportFragmentInjector,
                                HasFragmentInjector {
  @Inject
  lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

  @Inject
  lateinit var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    setTheme(appearanceSettings.theme.style)
    super.onCreate(savedInstanceState)
  }

  override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
    return supportFragmentInjector
  }

  override fun fragmentInjector(): AndroidInjector<android.app.Fragment>? {
    return frameworkFragmentInjector
  }
}
