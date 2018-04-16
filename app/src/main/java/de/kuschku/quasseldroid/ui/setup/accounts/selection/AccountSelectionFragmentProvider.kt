package de.kuschku.quasseldroid.ui.setup.accounts.selection

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AccountSelectionFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindAccountSelectionSlide(): AccountSelectionSlide
}
