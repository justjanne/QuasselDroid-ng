package de.kuschku.quasseldroid.ui.setup.accounts.setup

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AccountSetupFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindAccountSetupConnectionSlide(): AccountSetupConnectionSlide

  @ContributesAndroidInjector
  abstract fun bindAccountSetupNameSlide(): AccountSetupNameSlide

  @ContributesAndroidInjector
  abstract fun bindAccountSetupUserSlide(): AccountSetupUserSlide
}
