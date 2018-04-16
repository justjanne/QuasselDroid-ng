package de.kuschku.quasseldroid.ui.clientsettings.app

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppSettingsFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindAppSettingsFragment(): AppSettingsFragment
}
