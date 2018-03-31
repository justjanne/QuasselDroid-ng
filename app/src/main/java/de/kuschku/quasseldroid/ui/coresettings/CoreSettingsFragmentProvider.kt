package de.kuschku.quasseldroid.ui.coresettings

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CoreSettingsFragmentProvider {
  @ContributesAndroidInjector
  abstract fun CoreSettingsFragmentbind(): CoreSettingsFragment
}