package de.kuschku.quasseldroid.ui.coresettings.highlightlist

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HighlightListFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindHighlightListFragment(): HighlightListFragment
}
