package de.kuschku.quasseldroid.ui.clientsettings.whitelist

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class WhitelistFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindWhitelistFragment(): WhitelistFragment
}
