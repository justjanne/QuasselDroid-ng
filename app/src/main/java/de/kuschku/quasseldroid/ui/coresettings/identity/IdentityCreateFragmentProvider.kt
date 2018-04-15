package de.kuschku.quasseldroid.ui.coresettings.identity

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IdentityCreateFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindIdentityCreateFragment(): IdentityCreateFragment
}
