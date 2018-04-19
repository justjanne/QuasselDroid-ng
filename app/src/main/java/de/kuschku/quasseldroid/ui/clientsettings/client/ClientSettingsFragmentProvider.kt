package de.kuschku.quasseldroid.ui.clientsettings.client

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ClientSettingsFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindClientSettingsFragment(): ClientSettingsFragment
}
