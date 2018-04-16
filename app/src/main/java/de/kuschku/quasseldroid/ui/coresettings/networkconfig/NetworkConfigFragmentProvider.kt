package de.kuschku.quasseldroid.ui.coresettings.networkconfig

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NetworkConfigFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindNetworkConfigFragment(): NetworkConfigFragment
}
