package de.kuschku.quasseldroid.ui.settings

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SettingsFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindSettingsFragment(): SettingsFragment
}