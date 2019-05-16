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

package de.kuschku.quasseldroid.ui.setup.core

import androidx.fragment.app.FragmentActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CoreSetupFragmentProvider {
  @Binds
  abstract fun bindFragmentActivity(activity: CoreSetupActivity): FragmentActivity

  @ContributesAndroidInjector
  abstract fun bindCoreStorageBackendChooseSlide(): CoreStorageBackendChooseSlide

  @ContributesAndroidInjector
  abstract fun bindCoreStorageBackendSetupSlide(): CoreStorageBackendSetupSlide

  @ContributesAndroidInjector
  abstract fun bindCoreAuthenticatorBackendChooseSlide(): CoreAuthenticatorBackendChooseSlide

  @ContributesAndroidInjector
  abstract fun bindCoreAuthenticatorBackendSetupSlide(): CoreAuthenticatorBackendSetupSlide
}
