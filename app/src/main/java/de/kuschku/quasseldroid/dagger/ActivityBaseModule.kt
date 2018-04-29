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

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v4.app.FragmentActivity
import dagger.Module
import dagger.Provides
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountViewModel
import de.kuschku.quasseldroid.viewmodel.EditorViewModel
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel

@Module
object ActivityBaseModule {
  @Provides
  @JvmStatic
  fun bindContext(activity: FragmentActivity): Context = activity

  @Provides
  @JvmStatic
  fun provideViewModelProvider(activity: FragmentActivity) = ViewModelProviders.of(activity)

  @Provides
  @JvmStatic
  fun provideQuasselViewModel(viewModelProvider: ViewModelProvider) =
    viewModelProvider[QuasselViewModel::class.java]

  @Provides
  @JvmStatic
  fun provideAccountViewModel(viewModelProvider: ViewModelProvider) =
    viewModelProvider[AccountViewModel::class.java]

  @Provides
  @JvmStatic
  fun provideEditorViewModel(viewModelProvider: ViewModelProvider) =
    viewModelProvider[EditorViewModel::class.java]
}
