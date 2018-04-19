package de.kuschku.quasseldroid.ui.clientsettings.crash

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class CrashFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindClientSettingsFragment(): CrashFragment
}
