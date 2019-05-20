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

package de.kuschku.quasseldroid.dagger

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountViewModel
import de.kuschku.quasseldroid.viewmodel.*

@Module
object ActivityBaseModule {
  @ActivityScope
  @Provides
  @JvmStatic
  fun bindContext(activity: FragmentActivity): Context = activity

  @ActivityScope
  @Provides
  @JvmStatic
  fun provideViewModelProvider(activity: FragmentActivity) = ViewModelProviders.of(activity)

  @ActivityScope
  @Provides
  @JvmStatic
  fun provideQuasselViewModel(viewModelProvider: ViewModelProvider) =
    viewModelProvider[QuasselViewModel::class.java]

  @ActivityScope
  @Provides
  @JvmStatic
  fun provideChatViewModel(viewModelProvider: ViewModelProvider) =
    viewModelProvider[ChatViewModel::class.java]

  @ActivityScope
  @Provides
  @JvmStatic
  fun provideEditorViewModel(viewModelProvider: ViewModelProvider) =
    viewModelProvider[EditorViewModel::class.java]

  @ActivityScope
  @Provides
  @JvmStatic
  fun provideAccountViewModel(viewModelProvider: ViewModelProvider) =
    viewModelProvider[AccountViewModel::class.java]

  @ActivityScope
  @Provides
  @JvmStatic
  fun provideQueryCreateViewModel(viewModelProvider: ViewModelProvider) =
    viewModelProvider[QueryCreateViewModel::class.java]

  @ActivityScope
  @Provides
  @JvmStatic
  fun provideArchiveViewModel(viewModelProvider: ViewModelProvider) =
    viewModelProvider[ArchiveViewModel::class.java]
}
