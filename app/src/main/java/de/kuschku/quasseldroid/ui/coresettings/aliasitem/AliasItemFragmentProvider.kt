package de.kuschku.quasseldroid.ui.coresettings.aliasitem

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AliasItemFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindAliasItemFragment(): AliasItemFragment
}
