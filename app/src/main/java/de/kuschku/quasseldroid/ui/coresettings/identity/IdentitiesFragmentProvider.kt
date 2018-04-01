package de.kuschku.quasseldroid.ui.coresettings.identity

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IdentitiesFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindIdentitiesFragment(): IdentitiesFragment
}
