package de.kuschku.quasseldroid.ui.settings.license

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LicenseSettingsFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindLicenseSettingsFragment(): LicenseSettingsFragment
}