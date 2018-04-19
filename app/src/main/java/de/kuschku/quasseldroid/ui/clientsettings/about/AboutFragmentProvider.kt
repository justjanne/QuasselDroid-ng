package de.kuschku.quasseldroid.ui.clientsettings.about

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AboutFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindAboutFragment(): AboutFragment
}
