package de.kuschku.quasseldroid.ui.coresettings.network

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NetworkCreateFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindNetworkCreateFragment(): NetworkCreateFragment
}
