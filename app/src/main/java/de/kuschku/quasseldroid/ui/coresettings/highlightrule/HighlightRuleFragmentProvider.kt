package de.kuschku.quasseldroid.ui.coresettings.highlightrule

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HighlightRuleFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindHighlightRuleFragment(): HighlightRuleFragment
}
