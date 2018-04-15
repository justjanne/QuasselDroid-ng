package de.kuschku.quasseldroid.ui.coresettings.network

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NetworkEditFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindNetworkEditFragment(): NetworkEditFragment
}
