package de.kuschku.quasseldroid.ui.coresettings.aliaslist

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AliasListFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindAliasListFragment(): AliasListFragment
}
