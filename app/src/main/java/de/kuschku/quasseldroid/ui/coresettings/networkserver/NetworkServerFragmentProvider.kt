package de.kuschku.quasseldroid.ui.coresettings.networkserver

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NetworkServerFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindNetworkServerFragment(): NetworkServerFragment
}
