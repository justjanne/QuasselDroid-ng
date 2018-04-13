package de.kuschku.quasseldroid.ui.coresettings.ignoreitem

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IgnoreItemFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindIgnoreItemFragment(): IgnoreItemFragment
}
