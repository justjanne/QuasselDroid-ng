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
