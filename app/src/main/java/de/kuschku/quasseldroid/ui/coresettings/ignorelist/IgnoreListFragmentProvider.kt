package de.kuschku.quasseldroid.ui.coresettings.ignorelist

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IgnoreListFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindIgnoreFragment(): IgnoreListFragment
}
