package de.kuschku.quasseldroid.ui.settings.about

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AboutSettingsFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindAboutSettingsFragment(): AboutSettingsFragment
}