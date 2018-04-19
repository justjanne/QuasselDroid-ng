package de.kuschku.quasseldroid.ui.clientsettings.license

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LicenseFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindLicenseFragment(): LicenseFragment
}
