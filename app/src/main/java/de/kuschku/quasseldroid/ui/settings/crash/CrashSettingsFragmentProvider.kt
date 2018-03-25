package de.kuschku.quasseldroid.ui.settings.crash

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CrashSettingsFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindAppSettingsFragment(): CrashSettingsFragment
}